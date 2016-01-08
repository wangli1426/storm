package backtype.storm.elasticity;

import backtype.storm.elasticity.common.RouteId;
import backtype.storm.elasticity.config.Config;
import backtype.storm.elasticity.exceptions.BucketNotExistingException;
import backtype.storm.elasticity.message.actormessage.ElasticTaskMigrationConfirmMessage;
import backtype.storm.elasticity.message.actormessage.ElasticTaskMigrationMessage;
import backtype.storm.elasticity.actors.Slave;
import backtype.storm.elasticity.exceptions.InvalidRouteException;
import backtype.storm.elasticity.exceptions.RoutingTypeNotSupportedException;
import backtype.storm.elasticity.exceptions.TaskNotExistingException;
import backtype.storm.elasticity.message.taksmessage.*;
import backtype.storm.elasticity.networking.MyContext;
import backtype.storm.elasticity.resource.ResourceMonitor;
import backtype.storm.elasticity.routing.BalancedHashRouting;
import backtype.storm.elasticity.routing.PartialHashingRouting;
import backtype.storm.elasticity.routing.RoutingTable;
import backtype.storm.elasticity.routing.RoutingTableUtils;
import backtype.storm.elasticity.state.*;
import backtype.storm.elasticity.utils.FirstFitDoubleDecreasing;
import backtype.storm.elasticity.utils.Histograms;
import backtype.storm.elasticity.utils.timer.SmartTimer;
import backtype.storm.elasticity.utils.timer.SubtaskMigrationTimer;
import backtype.storm.elasticity.utils.timer.SubtaskWithdrawTimer;
import backtype.storm.generated.HostNotExistException;
import backtype.storm.messaging.IConnection;
import backtype.storm.messaging.IContext;
import backtype.storm.messaging.TaskMessage;
import backtype.storm.messaging.netty.Context;
import backtype.storm.task.WorkerTopologyContext;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.TupleImpl;
import backtype.storm.utils.Utils;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by Robert on 11/3/15.
 */
public class ElasticTaskHolder {

    public static Logger LOG = LoggerFactory.getLogger(ElasticTaskHolder.class);

    private static ElasticTaskHolder _instance;

    private IContext _context;

    private IConnection _inputConnection;

    private IConnection _priorityInputConnection;

    private IConnection _remoteExecutionResultInputConnection;

    private String _workerId;

    private int _port;

    public Slave _slaveActor;

    private WorkerTopologyContext _workerTopologyContext;

    Map<Integer, BaseElasticBoltExecutor> _bolts = new HashMap<>();

    Map<Integer, ElasticRemoteTaskExecutor> _originalTaskIdToRemoteTaskExecutor = new HashMap<>();

    Map<Integer, IConnection> _originalTaskIdToConnection = new ConcurrentHashMap<>();

    Map<Integer, IConnection> _originalTaskIdToExecutorResultConnection = new ConcurrentHashMap<>();

    Map<Integer, IConnection> _originalTaskIdToPriorityConnection = new ConcurrentHashMap<>();

    LinkedBlockingQueue<ITaskMessage> _sendingQueue = new LinkedBlockingQueue<>(Config.ElasticTaskHolderOutputQueueCapacity);

    Map<String, Semaphore> _taskidRouteToStateWaitingSemaphore = new ConcurrentHashMap<>();

    Map<String, Semaphore> _taskIdRouteToSendingWaitingSemaphore = new ConcurrentHashMap<>();

    private Map<String, IConnection> _taskidRouteToConnection = new HashMap<>();

    ResourceMonitor resourceMonitor;

    Map<RouteId, String> routeIdToRemoteHost = new HashMap<>();

    static public ElasticTaskHolder instance() {
        return _instance;
    }

    static public ElasticTaskHolder createAndGetInstance(Map stormConf, String workerId, int port) {
        if(_instance==null) {
            _instance = new ElasticTaskHolder(stormConf, workerId, port);
        }
        return _instance;
    }

    private ElasticTaskHolder(Map stormConf, String workerId, int port) {
        System.out.println("creating ElasticTaskHolder");
        _context = new Context();
        _port = port + 10000;
        _context.prepare(stormConf);
        _inputConnection = _context.bind(workerId,_port);
        _priorityInputConnection = _context.bind(workerId, _port + 5);
        _remoteExecutionResultInputConnection = _context.bind(workerId, _port + 10);
        _workerId = workerId;
        _slaveActor = Slave.createActor(_workerId,Integer.toString(port));
        if(_slaveActor == null)
            System.out.println("NOTE: _slaveActor is null!!***************\n");
        createExecuteResultReceivingThread();
        createExecuteResultSendingThread();
        createPriorityReceivingThread();
        createRemoteExecutorResultReceivingThread();
        LOG.info("ElasticTaskHolder is launched.");
        LOG.info("storm id:"+workerId+" port:" + port);
        Utils.sleep(2000);
        _slaveActor.sendMessageToMaster("My pid is: " + ManagementFactory.getRuntimeMXBean().getName());
        resourceMonitor = new ResourceMonitor();
    }

    public void registerElasticBolt(BaseElasticBoltExecutor bolt, int taskId) {
        _bolts.put(taskId, bolt);
        _slaveActor.registerOriginalElasticTaskToMaster(taskId);
        LOG.info("A new ElasticTask is registered." + taskId);
    }

    public void sendMessageToMaster(String message) {
        _slaveActor.sendMessageToMaster(message);
    }


    public ElasticTaskMigrationMessage generateRemoteElasticTasks(int taskid, int route) throws RoutingTypeNotSupportedException, InvalidRouteException {
        if(!_bolts.containsKey(taskid)){
            System.err.println("task "+taskid+" does not exist! Remember to use withdraw command if you want to move a remote subtask to the original host!");
            throw new RuntimeException("task "+taskid+" does not exist! Remember to use withdraw command if you want to move a remote subtask to the original host!");
        }

        System.out.println("Add exceptions to the routing table...");
        /* set exceptions for existing routing table and get the complement routing table */
        PartialHashingRouting complementHashingRouting = _bolts.get(taskid).get_elasticTasks().addExceptionForHashRouting(route, _sendingQueue);

//        if(complementHashingRouting==null) {
//            return null;
//        }

        System.out.println("Constructing the instance of ElasticTask for remote execution...");
        /* construct the instance of ElasticTasks to be executed remotely */
        ElasticTasks existingElasticTasks = _bolts.get(taskid).get_elasticTasks();
        ElasticTasks remoteElasticTasks = new ElasticTasks(existingElasticTasks.get_bolt(),existingElasticTasks.get_taskID());
        remoteElasticTasks.set_routingTable(complementHashingRouting);

        System.out.println("Packing the involved state...");
        KeyValueState existingState = existingElasticTasks.get_bolt().getState();

        KeyValueState state = new KeyValueState();

        for(Object key: existingState.getState().keySet()) {
            if(complementHashingRouting.route(key)>=0) {
                state.setValueBySey(key, existingState.getValueByKey(key));
//                System.out.println("State <"+key+","+existingState.getValueByKey(key)+"> will be migrated!");
            } else {
//                System.out.println("State <"+key+","+existingState.getValueByKey(key)+"> will be ignored!");
            }
        }
        System.out.println("State for migration is ready!");

        return new ElasticTaskMigrationMessage(remoteElasticTasks, _port, state);
    }

    public ElasticTaskMigrationConfirmMessage handleGuestElasticTasks(ElasticTaskMigrationMessage message) {
        sendMessageToMaster("ElasticTaskMigrationMessage: "+ message.getString());
        System.out.println("ElasticTaskMigrationMessage: "+ message.getString());
        System.out.println("#. of routes"+message._elasticTask.get_routingTable().getRoutes().size());
//        _remoteTasks.put(message._elasticTask.get_taskID(), message._elasticTask);
        IConnection iConnection = _context.connect(message._ip + ":" + message._port + "-" + message._elasticTask.get_taskID(),message._ip,message._port);
        _originalTaskIdToConnection.put(message._elasticTask.get_taskID(),iConnection);

        IConnection prioritizedConnection = _context.connect(message._ip + ":" + (message._port + 5) + "-" + message._elasticTask.get_taskID(), message._ip, message._port +5);
        _originalTaskIdToPriorityConnection.put(message._elasticTask.get_taskID(), prioritizedConnection);

        IConnection remoteExecutionResultConnection = _context.connect(message._ip + ":" + (message._port + 10) + "-" + message._elasticTask.get_taskID(), message._ip, message._port +10);
        _originalTaskIdToExecutorResultConnection.put(message._elasticTask.get_taskID(), remoteExecutionResultConnection);

        System.out.println("Connected with original Task Holders");
        sendMessageToMaster("Connected with original Task holders!");


        if(!_originalTaskIdToRemoteTaskExecutor.containsKey(message._elasticTask.get_taskID())) {
            //This is the first RemoteTasks assigned to this host.

            ElasticRemoteTaskExecutor remoteTaskExecutor = new ElasticRemoteTaskExecutor(message._elasticTask, _sendingQueue, message._elasticTask.get_bolt());

            System.out.println("ElasticRemoteTaskExecutor is created!");

            _originalTaskIdToRemoteTaskExecutor.put(message._elasticTask.get_taskID(), remoteTaskExecutor);

            System.out.println("ElasticRemoteTaskExecutor is added to the map!");
            remoteTaskExecutor.prepare(message.state);
            System.out.println("ElasticRemoteTaskExecutor is prepared!");
//            remoteTaskExecutor.createProcessingThread();
            System.out.println("Remote Task Executor is launched");
        } else {
            //There is already a RemoteTasks for that tasks on this host, so we just need to update the routing table
            //and create processing thread accordingly.


            ElasticRemoteTaskExecutor remoteTaskExecutor = _originalTaskIdToRemoteTaskExecutor.get(message._elasticTask.get_taskID());
            remoteTaskExecutor._elasticTasks.get_bolt().getState().update(message.state);
//            for(Object key: message.state.getState().keySet()) {
//                System.out.println("State <"+key+", "+ message.state.getValueByKey(key)+"> has been restored!");
//            }
            System.out.println("Received original state!");
            remoteTaskExecutor.mergeRoutingTableAndCreateCreateWorkerThreads(message._elasticTask.get_routingTable());


        }

        ElasticTaskMigrationConfirmMessage confirmMessage = new ElasticTaskMigrationConfirmMessage(message._elasticTask.get_taskID(), _slaveActor.getIp() , _port, message._elasticTask.get_routingTable().getRoutes() );


        return confirmMessage;

    }

    private void createExecuteResultSendingThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Integer count = 0;
                while (true) {
                    try {
                        System.out.println("fetching...");
                        final ITaskMessage message = _sendingQueue.take();
                        System.out.println("sending...");
                        LOG.debug("An element is taken from the sendingQueue");
                        if(message instanceof RemoteTupleExecuteResult) {
                            LOG.debug("The element is RemoteTupleExecuteResult");

                            RemoteTupleExecuteResult remoteTupleExecuteResult = (RemoteTupleExecuteResult)message;
                            if (_originalTaskIdToExecutorResultConnection.containsKey(remoteTupleExecuteResult._originalTaskID)) {
                                byte[] bytes = SerializationUtils.serialize(remoteTupleExecuteResult);
                                _originalTaskIdToExecutorResultConnection.get(remoteTupleExecuteResult._originalTaskID).send(remoteTupleExecuteResult._originalTaskID, bytes);
                                LOG.debug("RemoteTupleExecutorResult is send back!");
                            } else {
                                System.err.println("RemoteTupleExecuteResult will be ignored, because we cannot find the connection for tasks " + remoteTupleExecuteResult._originalTaskID);
                            }
                        } else if (message instanceof RemoteTuple) {

                            LOG.debug("The element is RemoteTuple");
                            RemoteTuple remoteTuple = (RemoteTuple) message;

                            final String key = remoteTuple.taskIdAndRoutePair();
                            LOG.debug("Key :"+key);
                            if(_taskidRouteToConnection.containsKey(key)) {
                                LOG.debug("The element will be serialized!");
                                final byte[] bytes = SerializationUtils.serialize(remoteTuple);
                                _taskidRouteToConnection.get(key).send(remoteTuple._taskId, bytes);
                                LOG.debug("RemoteTuple is sent!");
                            } else {
                                System.err.println("RemoteTuple will be ignored, because we cannot find connection for remote tasks " + remoteTuple.taskIdAndRoutePair());
                            }

                        } else if (message instanceof RemoteSubtaskTerminationToken) {
                            RemoteSubtaskTerminationToken remoteSubtaskTerminationToken = (RemoteSubtaskTerminationToken) message;
                            final String key = remoteSubtaskTerminationToken.taskid + "." + remoteSubtaskTerminationToken.route;
                            if(_taskidRouteToConnection.containsKey(key)) {
                                final byte[] bytes = SerializationUtils.serialize(remoteSubtaskTerminationToken);
                                _taskidRouteToConnection.get(key).send(remoteSubtaskTerminationToken.taskid, bytes);
                                System.out.println("RemoteSubtaskTerminationToken is sent");
                            } else {
                                System.err.println("RemoteSubtaskTerminationToken does not have a valid taskid and route: " +key);
                            }

                        } else if (message instanceof RemoteState) {
                            RemoteState remoteState = (RemoteState) message;
                            byte[] bytes = SerializationUtils.serialize(remoteState);
                            IConnection connection = _originalTaskIdToConnection.get(remoteState._taskId);
                            if(connection != null) {
                                if(!remoteState.finalized&& !_originalTaskIdToRemoteTaskExecutor.containsKey(remoteState._taskId)) {
                                    System.out.println("Remote state is ignored to send, as the state is not finalized ans the original RemoteTaskExecutor does not exist!");
                                    continue;
                                }
                                connection.send(remoteState._taskId, bytes);
                                System.out.println("RemoteState is sent back!");
                            } else {
                                System.err.println("Cannot find the connection for task " + remoteState._state);
                                System.out.println("TaskId: " + remoteState._taskId);
                                System.out.println("Connections: " + _originalTaskIdToConnection );
                            }
                        } else {
                            System.err.print("Unknown element from the sending queue");
                        }
                        System.out.println("sent...");
                    } catch (InterruptedException e) {
                        System.out.println("sending thread has been interrupted!");
                    } catch (SerializationException ex) {
                        System.err.println("Serialization Error!");
                        ex.printStackTrace();
                    } catch (Exception eee) {
                        eee.printStackTrace();
                    }
                }
            }
        }).start();
        System.out.println("sending thread is created!");
    }

    private void createPriorityReceivingThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Iterator<TaskMessage> messageIterator = _priorityInputConnection.recv(0, 0);
                        while(messageIterator.hasNext()) {
                            TaskMessage message = messageIterator.next();
                            Object object = SerializationUtils.deserialize(message.message());
                            if(object instanceof RemoteState) {
                                System.out.println("Received RemoteState!");
                                RemoteState remoteState = (RemoteState) object;
                                handleRemoteState(remoteState);
                            } else {
                                System.err.println("Priority input connection receives unexpected object: " + object);
                                _slaveActor.sendMessageToMaster("Priority input connection receives unexpected object: " + object);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void createRemoteExecutorResultReceivingThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Iterator<TaskMessage> messageIterator = _remoteExecutionResultInputConnection.recv(0, 0);
                        while(messageIterator.hasNext()) {
                            TaskMessage message = messageIterator.next();
                            Object object = SerializationUtils.deserialize(message.message());
                            if(object instanceof RemoteTupleExecuteResult) {
                                RemoteTupleExecuteResult result = (RemoteTupleExecuteResult)object;
                                ((TupleImpl)result._inputTuple).setContext(_workerTopologyContext);
                                LOG.debug("A query result is received for "+result._originalTaskID);
                                _bolts.get(result._originalTaskID).insertToResultQueue(result);
                                LOG.debug("a query result tuple is added into the input queue");
                            } else {
                                System.err.println("Remote Execution Result input connection receives unexpected object: " + object);
                                _slaveActor.sendMessageToMaster("Remote Execution Result input connection receives unexpected object: " + object);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void createExecuteResultReceivingThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                try {
                    System.out.println("receiving...");
                    Iterator<TaskMessage> messageIterator = _inputConnection.recv(0, 0);
                    while(messageIterator.hasNext()) {
                        TaskMessage message = messageIterator.next();
                        int targetTaskId = message.task();
                        Object object = SerializationUtils.deserialize(message.message());
                        System.out.println("Received " + object);
                        if(object instanceof RemoteTupleExecuteResult) {
                            RemoteTupleExecuteResult result = (RemoteTupleExecuteResult)object;
                            ((TupleImpl)result._inputTuple).setContext(_workerTopologyContext);
                            LOG.debug("A query result is received for "+result._originalTaskID);
                            _bolts.get(targetTaskId).insertToResultQueue(result);
                            LOG.debug("a query result tuple is added into the input queue");
                        } else if (object instanceof RemoteTuple) {
                            RemoteTuple remoteTuple = (RemoteTuple) object;
                            try {
                                LOG.debug("A remote tuple %d.%d is received!\n", remoteTuple._taskId, remoteTuple._route);
                                ((TupleImpl)remoteTuple._tuple).setContext(_workerTopologyContext);
                                ElasticRemoteTaskExecutor elasticRemoteTaskExecutor = _originalTaskIdToRemoteTaskExecutor.get(remoteTuple._taskId);
                                LinkedBlockingQueue<Tuple> queue = elasticRemoteTaskExecutor.get_inputQueue();
                                queue.put(remoteTuple._tuple);
                                LOG.debug("A remote tuple is added to the queue!\n");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } else if (object instanceof RemoteState) {
                            System.out.println("Received RemoteState!");
                            RemoteState remoteState = (RemoteState) object;
                            handleRemoteState(remoteState);

                        } else if (object instanceof RemoteSubtaskTerminationToken) {
                            System.out.print("Received a RemoteSubtaskTerminationToken!");
                            RemoteSubtaskTerminationToken remoteSubtaskTerminationToken = (RemoteSubtaskTerminationToken) object;
                            terminateRemoteRoute(remoteSubtaskTerminationToken.taskid, remoteSubtaskTerminationToken.route);
                        } else if (object instanceof BucketToRouteReassignment) {
                            sendMessageToMaster("Received BucketToRouteReassignment");
                            System.out.println("Received BucketToRouteReassignment");
                            BucketToRouteReassignment reassignment = (BucketToRouteReassignment)object;
                            handleBucketToRouteReassignment(reassignment);
                        } else if (object instanceof StateFlushToken) {
                            System.out.println("Received StateFlushToken!");
                            sendMessageToMaster("Received StateFlushToken!");
                            StateFlushToken token = (StateFlushToken) object;

                            handleStateFlushToken(token);


                        } else if (object instanceof String) {
                            System.out.println(object);
                        } else {
                            System.err.println("Unexpected Object: " + object);
                        }

                    }

                    } catch (Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void handleRemoteState(RemoteState remoteState) {
        if(_bolts.containsKey(remoteState._taskId)) {
            getState(remoteState._taskId).update(remoteState._state);
            System.out.println("State ("+remoteState._state.getState().size()+ " elements) has been updated!");
            if(remoteState.finalized) {
                System.out.println("Its finalized!");
                for(int route: remoteState._routes) {
                    if(_taskidRouteToStateWaitingSemaphore.containsKey(remoteState._taskId + "." + route)) {
                        _taskidRouteToStateWaitingSemaphore.get(remoteState._taskId+"."+route).release();
                        System.out.println("Semaphore for " + remoteState._taskId + "." + route + "has been released");
                    }
                }
            } else {
                System.out.println("It's now finalized!");
            }

        } else if (_originalTaskIdToRemoteTaskExecutor.containsKey(remoteState._taskId)) {
            getState(remoteState._taskId).update(remoteState._state);
            System.out.println("State ("+remoteState._state.getState().size()+ " elements) has been updated!");

//            if(_taskidRouteToConnection.containsKey(remoteState._taskId+"."+remoteState._routes.get(0))) {
//                _taskidRouteToConnection.get(remoteState._taskId+"."+remoteState._routes.get(0)).send(remoteState._taskId, SerializationUtils.serialize(remoteState));
//            } else {
//                System.err.println("Cannot find remote connection for  ["+remoteState._taskId+"."+remoteState._routes.get(0));
//            }


        }
    }

    private void handleStateFlushToken(StateFlushToken token) {

        //TODO: there should be some mechanism to guarantee that the state is flushed until all the tuple has been processed
        System.out.print("Breakpoint 1");
        KeyValueState partialState = getState(token._taskId).getValidState(token._filter);
        RemoteState remoteState = new RemoteState(token._taskId, partialState, token._targetRoute);
        remoteState.markAsFinalized();
        System.out.print("Breakpoint 2");
        if(_originalTaskIdToPriorityConnection.containsKey(token._taskId)) {
            _originalTaskIdToPriorityConnection.get(token._taskId).send(token._taskId,SerializationUtils.serialize(remoteState));
            sendMessageToMaster("Remote state is send back to the original elastic holder!");
            System.out.print("Remote state is send back to the original elastic holder!");
        } else {

            System.out.print("Remote state does not need to be sent, as the remote state is already balls the original holder!");
            sendMessageToMaster("Remote state does not need to be sent, as the remote state is already balls the original holder!");
//            handleRemoteState(remoteState); //@Li: This line is commented, as it seems that the state should not be migrate if the target subtask and the original subtask are in the same node.
        }
        System.out.print("Breakpoint 3");
    }

    private KeyValueState getState(int taskId) {
        if(_bolts.containsKey(taskId))
            return _bolts.get(taskId).get_elasticTasks().get_bolt().getState();
        else if (_originalTaskIdToRemoteTaskExecutor.containsKey(taskId)) {
            return _originalTaskIdToRemoteTaskExecutor.get(taskId)._bolt.getState();
        }
        return null;
    }

    private BalancedHashRouting getBalancedHashRoutingFromOriginalBolt(int taskid) {
        if(_bolts.containsKey(taskid)) {

                RoutingTable routingTable = _bolts.get(taskid).get_elasticTasks().get_routingTable();
                if(routingTable instanceof BalancedHashRouting) {
                    return (BalancedHashRouting)routingTable;
                } else if ((routingTable instanceof PartialHashingRouting) && (((PartialHashingRouting) routingTable).getOriginalRoutingTable() instanceof BalancedHashRouting)) {
                    return (BalancedHashRouting)((PartialHashingRouting) routingTable).getOriginalRoutingTable();
                }


            }
        return null;
    }

    private BalancedHashRouting getBalancedHashRoutingFromRemoteBolt(int taskid) {
        if(_originalTaskIdToRemoteTaskExecutor.containsKey(taskid)) {

            RoutingTable routingTable = _originalTaskIdToRemoteTaskExecutor.get(taskid)._elasticTasks.get_routingTable();
            if(routingTable instanceof BalancedHashRouting) {
                return (BalancedHashRouting)routingTable;
            } else if ((routingTable instanceof PartialHashingRouting) && (((PartialHashingRouting) routingTable).getOriginalRoutingTable() instanceof BalancedHashRouting)) {
                return (BalancedHashRouting)((PartialHashingRouting) routingTable).getOriginalRoutingTable();
            }
        }
        return null;
    }


    private void handleBucketToRouteReassignment(BucketToRouteReassignment reassignment) {
        if(_bolts.containsKey(reassignment.taskid)) {
            BalancedHashRouting balancedHashRouting = getBalancedHashRoutingFromOriginalBolt(reassignment.taskid);
            for(int bucket: reassignment.reassignment.keySet()) {
                balancedHashRouting.reassignBucketToRoute(bucket, reassignment.reassignment.get(bucket));
                sendMessageToMaster(bucket + " is reassigned to "+ reassignment.reassignment.get(bucket) + " in the original elastic task");
                System.out.println(bucket + " is reassigned to "+ reassignment.reassignment.get(bucket) + " in the original elastic task");
            }
        }
        if(_originalTaskIdToRemoteTaskExecutor.containsKey(reassignment.taskid)) {
            BalancedHashRouting balancedHashRouting = getBalancedHashRoutingFromRemoteBolt(reassignment.taskid);
            for(int bucket: reassignment.reassignment.keySet()) {
                balancedHashRouting.reassignBucketToRoute(bucket, reassignment.reassignment.get(bucket));
                sendMessageToMaster(bucket + " is reassigned to "+ reassignment.reassignment.get(bucket) + " in the remote elastic task");
                System.out.println(bucket + " is reassigned to "+ reassignment.reassignment.get(bucket) + " in the remote elastic task");
            }
        }
    }

    public void establishConnectionToRemoteTaskHolder(int taksId, int route, String remoteIp, int remotePort) {
        IConnection connection = _context.connect("",remoteIp,remotePort);
        _taskidRouteToConnection.put(taksId+"."+route, connection);
        System.out.println("Established connection with remote task holder!");
    }

    public void createRouting(int taskid, int numberOfRouting, String type) throws TaskNotExistingException,RoutingTypeNotSupportedException {
        if(!_bolts.containsKey(taskid)) {
            throw new TaskNotExistingException(taskid);
        }

        if(type.equals("balanced_hash")) {
            createBalancedHashRouting(taskid, numberOfRouting);
        } else {

            if(!type.equals("hash"))
                throw new RoutingTypeNotSupportedException("Only support hash routing now!");
            _bolts.get(taskid).get_elasticTasks().setHashRouting(numberOfRouting);
        }
        _slaveActor.sendMessageToMaster("New RoutingTable has been created!");
        System.out.println("RoutingTable has been created");


    }

    public void createBalancedHashRouting(int taskid, int numberOfRouting) throws TaskNotExistingException {
        if(!_bolts.containsKey(taskid)) {
            throw new TaskNotExistingException(taskid);
        }
        _bolts.get(taskid)._keyBucketSampler.clear();
        _bolts.get(taskid)._keyBucketSampler.enable();
        _slaveActor.sendMessageToMaster("It will take " + Config.CreateBalancedHashRoutingSamplingTimeInSecs + "seconds to sample the distribution of the input tuples on the key domain.");
        Utils.sleep(Config.CreateBalancedHashRoutingSamplingTimeInSecs * 1000);
        _bolts.get(taskid)._keyBucketSampler.disable();
        _slaveActor.sendMessageToMaster("Sampling completes");

        FirstFitDoubleDecreasing firstFitDoubleDecreasing = new FirstFitDoubleDecreasing(Arrays.asList(_bolts.get(taskid)._keyBucketSampler.buckets),numberOfRouting);

        final int result = firstFitDoubleDecreasing.getResult();
        if(result == numberOfRouting) {
            _slaveActor.sendMessageToMaster(firstFitDoubleDecreasing.toString());
            _bolts.get(taskid).get_elasticTasks().setHashBalancedRouting(numberOfRouting, firstFitDoubleDecreasing.getBucketToPartitionMap());



        } else {
            _slaveActor.sendMessageToMaster("Failed to partition the buckets!");
        }


    }

    public void withdrawRemoteElasticTasks(int taskid, int route) throws TaskNotExistingException, RoutingTypeNotSupportedException, InvalidRouteException {
        SubtaskWithdrawTimer.getInstance().start();
        if(!_bolts.containsKey(taskid)) {
            throw new TaskNotExistingException(taskid);
        }
        RoutingTable routingTable = _bolts.get(taskid).get_elasticTasks().get_routingTable();
        if(!(routingTable instanceof PartialHashingRouting)) {
            throw new RoutingTypeNotSupportedException("Can only withdraw remote tasks for PartialHashingRouting!");
        }
        PartialHashingRouting partialHashingRouting = (PartialHashingRouting) routingTable;
        if(!partialHashingRouting.getOriginalRoutes().contains(route)) {
            throw new InvalidRouteException("Route " + route + " is not valid");
        }
        if(partialHashingRouting.getRoutes().contains(route)) {
            throw new InvalidRouteException("Route " + route + " is not in exception list");
        }

        _bolts.get(taskid).get_elasticTasks().addValidRoute(route);
        System.out.println("Route " + route + " has been added into the routing table!");
        sendFinalTuple(taskid, route);

        SubtaskWithdrawTimer.getInstance().prepared();

        System.out.println("RemoteSubtaskTerminationToken has been sent!");


        _taskidRouteToStateWaitingSemaphore.put(taskid+ "."+route,new Semaphore(0));
        try {
            System.out.println("Waiting for the remote state");
            _taskidRouteToStateWaitingSemaphore.get(taskid + "." + route).acquire();

            System.out.println("Remote state arrives!");

            System.out.println("launch the thread for "+taskid+"."+route+".");
            _bolts.get(taskid).get_elasticTasks().launchElasticTasksForGivenRoute(route);
            System.out.println("Remote " + taskid + "." + route + "has been withdrawn!");
            _slaveActor.sendMessageToMaster("Remote " + taskid + "." + route + "has been withdrawn!");
            SubtaskWithdrawTimer.getInstance().terminated();
            _slaveActor.sendMessageToMaster(SubtaskWithdrawTimer.getInstance().toString());
            routeIdToRemoteHost.remove(new RouteId(taskid, route));
        } catch (InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private void sendFinalTuple(int taskid, int route) {

        RemoteSubtaskTerminationToken remoteSubtaskTerminationToken = new RemoteSubtaskTerminationToken(taskid, route);
        try {
            _sendingQueue.put(remoteSubtaskTerminationToken);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * First terminate the query thread and clear the associated resources until all the local tuples for the route has been processed
     * Then get the state for the route and send the state back to the original ElasticTasks.
     * Finally, remove the route balls the routingTable.
     * @param taskid the taskid
     * @param route the route to remove
     */
    private void terminateRemoteRoute(int taskid, int route) {
        //terminate the thread and cleanup the resources.
        ElasticRemoteTaskExecutor remoteTaskExecutor = _originalTaskIdToRemoteTaskExecutor.get(taskid);
        remoteTaskExecutor._elasticTasks.terminateGivenQuery(route);
        try {
            //get state and send back
            RemoteState state = remoteTaskExecutor.getStateForRoutes(route);
            state.markAsFinalized();
            _sendingQueue.put(state);
            System.out.println("Final State for " + taskid +"." + route + " has been sent back");


        } catch (InterruptedException e ) {

        }

        ((PartialHashingRouting)remoteTaskExecutor._elasticTasks.get_routingTable()).addExceptionRoute(route);
        System.out.println("Route "+ route+ " has been removed from the routing table");

        if(remoteTaskExecutor._elasticTasks.get_routingTable().getRoutes().size()==0) {
            removeEmptyRemoteTaskExecutor(taskid);
        }



    }

    private void removeEmptyRemoteTaskExecutor(int taskid) {
        ElasticRemoteTaskExecutor remoteTaskExecutor = _originalTaskIdToRemoteTaskExecutor.get(taskid);
        remoteTaskExecutor.close();
        _originalTaskIdToRemoteTaskExecutor.remove(taskid);

        System.out.println("RemoteTaskExecutor " + taskid + " is interrupted!");
    }

    public void setworkerTopologyContext(WorkerTopologyContext context) {
        _workerTopologyContext = context;
    }

    public WorkerTopologyContext getWorkerTopologyContext() {
        return _workerTopologyContext;
    }

    public double getThroughput(int taskid) {
        if(!_bolts.containsKey(taskid))
            return -1;
        return _bolts.get(taskid).getRate();
    }

    public Histograms getDistribution(int taskid) throws TaskNotExistingException {
        if(!_bolts.containsKey(taskid)) {
            throw new TaskNotExistingException("Task " + taskid + " does not exist!");
        } else {
            return _bolts.get(taskid).get_elasticTasks()._sample.getDistribution();
        }
    }

    public RoutingTable getRoutingTable(int taskid) throws TaskNotExistingException {
        if(!_bolts.containsKey(taskid)) {
            throw new TaskNotExistingException("task " + taskid + "does not exist!");
        } else {
            return _bolts.get(taskid).get_elasticTasks().get_routingTable();
        }
    }

    public void reassignHashBucketToRoute(int taskid, int bucketId, int orignalRoute, int targetRoute) throws TaskNotExistingException, RoutingTypeNotSupportedException, InvalidRouteException, BucketNotExistingException {
        SmartTimer.getInstance().start("ShardReassignment","total");
        SmartTimer.getInstance().start("ShardReassignment","prepare");

        if(!_bolts.containsKey(taskid)) {
            throw new TaskNotExistingException("Task " + taskid + " does not exist balls the ElasticHolder!");
        }
        if(getBalancedHashRoutingFromOriginalBolt(taskid)==null) {
            throw new RoutingTypeNotSupportedException("ReassignHashBucketToRoute only applies on BalancedHashRouting or PartialHashRouting with a internal BalancedHashRouting");
        }

        BalancedHashRouting balancedHashRouting;// = (BalancedHashRouting)_bolts.get(taskid).get_elasticTasks().get_routingT  
        if(_bolts.get(taskid).get_elasticTasks().get_routingTable() instanceof BalancedHashRouting) {
            balancedHashRouting = (BalancedHashRouting)_bolts.get(taskid).get_elasticTasks().get_routingTable();
        } else {
            balancedHashRouting = (BalancedHashRouting)((PartialHashingRouting)_bolts.get(taskid).get_elasticTasks().get_routingTable()).getOriginalRoutingTable();
        }

        if(!(balancedHashRouting.getRoutes().contains(orignalRoute))) {
            throw new InvalidRouteException("Original Route " + orignalRoute + " does not exist!");
        }

        if(!(balancedHashRouting.getRoutes().contains(targetRoute))) {
            throw new InvalidRouteException("Target Route " + targetRoute + " does not exist!");
        }


        if(!balancedHashRouting.getBucketSet().contains(bucketId)) {
            throw new BucketNotExistingException("Bucket " + bucketId + " does not exist balls the balanced hash routing table!");
        }

        BucketToRouteReassignment reassignment = new BucketToRouteReassignment(taskid, bucketId, targetRoute);

        String targetHost, originalHost;
        if(routeIdToRemoteHost.containsKey(new RouteId(taskid, orignalRoute)))
            originalHost = routeIdToRemoteHost.get(new RouteId(taskid, orignalRoute));
        else
            originalHost = "local";

        if(routeIdToRemoteHost.containsKey(new RouteId(taskid, targetRoute)))
            targetHost = routeIdToRemoteHost.get(new RouteId(taskid, targetRoute));
        else
            targetHost = "local";

        SmartTimer.getInstance().stop("ShardReassignment", "prepare");

        // Update the routing table on the target subtask
        if(_taskidRouteToConnection.containsKey(taskid+"."+targetRoute)){
            _taskidRouteToConnection.get(taskid+"."+targetRoute).send(taskid, SerializationUtils.serialize(reassignment));
        }
//        else {
//            handleBucketToRouteReassignment(reassignment);
//        }


        SmartTimer.getInstance().start("ShardReassignment","rerouting");
        // Pause sending RemoteTuples to the target subtask
//        pauseSendingToTargetSubtask(taskid, targetRoute);
//        pauseSendingToTargetSubtask(taskid, orignalRoute);

        // Update the routing table on original ElasticTaskHolder
        handleBucketToRouteReassignment(reassignment);

        // Update the routing table on the source
            int ran = new Random().nextInt();
        if(_taskidRouteToConnection.containsKey(taskid+"."+orignalRoute)){
            sendMessageToMaster("BucketToRouteReassignment is sent to the original Host " + ran);
            _taskidRouteToConnection.get(taskid+"."+orignalRoute).send(taskid, SerializationUtils.serialize(reassignment));
            for(int i=0; i< 10000; i++) {
                _taskidRouteToConnection.get(taskid+"."+orignalRoute).send(taskid, SerializationUtils.serialize("-_- " + ran + " seq:" + i));
            }
        }
        SmartTimer.getInstance().stop("ShardReassignment", "rerouting");
        SmartTimer.getInstance().start("ShardReassignment","state migration");
        sendMessageToMaster("Begin state migration session!");

        // 3. handle state for that shard, if necessary
        if(!targetHost.equals(originalHost)) {
                HashBucketFilter filter = new HashBucketFilter(balancedHashRouting.getNumberOfBuckets(), bucketId);
            if(!originalHost.equals("local")) {
                StateFlushToken stateFlushToken = new StateFlushToken(taskid, orignalRoute, filter);
                _taskidRouteToConnection.get(taskid+ "." + orignalRoute).send(taskid, SerializationUtils.serialize(stateFlushToken));
                _slaveActor.sendMessageToMaster("State Flush Token has been sent to " + originalHost);
                for(int i=0; i< 10000; i++) {
                    _taskidRouteToConnection.get(taskid+"."+orignalRoute).send(taskid, SerializationUtils.serialize("^_^ " + ran + " seq:" + i));
                }
                _taskidRouteToStateWaitingSemaphore.put(taskid+ "." + orignalRoute, new Semaphore(0));
                try {
                    _slaveActor.sendMessageToMaster("Waiting for remote state!");
                    _taskidRouteToStateWaitingSemaphore.get(taskid+ "." + orignalRoute).acquire();
                    _slaveActor.sendMessageToMaster("Remote state arrived!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                _slaveActor.sendMessageToMaster("State for the shard does not need to be flushed, as the source subtask is run on the original host!");
            }
            if (!targetHost.equals("local")) {
                KeyValueState partialState = getState(taskid).getValidState(filter);
                RemoteState remoteState = new RemoteState(taskid, partialState, targetRoute);
                _taskidRouteToConnection.get(taskid + "." + targetRoute).send(taskid, SerializationUtils.serialize(remoteState));
                sendMessageToMaster("State has been sent to " + targetHost);
            } else {
                _slaveActor.sendMessageToMaster("State for the shard does not need to migrate, as the target subtask is run on the original host!");
            }
        } else {
            _slaveActor.sendMessageToMaster("State movement is not necessary, as the shard is moved within a host!");
        }
        SmartTimer.getInstance().stop("ShardReassignment", "state migration");

        // 5. resume sending RemoteTuples to the target subtask
        System.out.println("Begin to resume!");
//        resumeSendingToTargetSubtask(taskid, targetRoute);
//        resumeSendingToTargetSubtask(taskid, orignalRoute);
        System.out.println("Resumed!");
        SmartTimer.getInstance().stop("ShardReassignment", "total"); 
        _slaveActor.sendMessageToMaster(SmartTimer.getInstance().getTimerString("ShardReassignment"));

    }

    public void waitIfStreamToTargetSubtaskIsPaused(int targetTask, int route) {
//        System.out.println("waitIfStreamToTargetSubtaskIsPaused!");
        String key = targetTask+"."+route;
        if(_taskIdRouteToSendingWaitingSemaphore.containsKey(key)) {
            try {
                System.out.println("Sending stream to " + targetTask + "." + route + " is paused. Waiting for resumption!");
                _taskIdRouteToSendingWaitingSemaphore.get(key).acquire();
                _taskIdRouteToSendingWaitingSemaphore.remove(key);
                System.out.println( targetTask + "." + route +" is resumed!!!!!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void pauseSendingToTargetSubtask(int targetTask, int route) {
        String key = targetTask+"."+route;
        if(_taskIdRouteToSendingWaitingSemaphore.containsKey(key)) {
            System.out.println(key+ " already exists in the Semaphore mapping!");
            return;
        }

        _taskIdRouteToSendingWaitingSemaphore.put(key, new Semaphore(0));
        System.out.println("Sending to " + key + " is paused!");

    }

    void resumeSendingToTargetSubtask(int targetTask, int route) {
        String key = targetTask + "." + route;
        if(!_taskIdRouteToSendingWaitingSemaphore.containsKey(key)) {
            System.out.println("cannot resume "+key+" because the semaphore does not exist!");
            return;
        }
        _taskIdRouteToSendingWaitingSemaphore.get(key).release();
        System.out.println("Sending is resumed!");

    }

    public Histograms getBucketDistributionForBalancedRoutingTable(int taskId) {
        if(!_bolts.containsKey(taskId)) {
            System.out.println("Task " + taskId + "does not exist!");
            return null;
        }
        RoutingTable routingTable = _bolts.get(taskId).get_elasticTasks().get_routingTable();
        return ((BalancedHashRouting)RoutingTableUtils.getBalancecHashRouting(routingTable)).getBucketsDistribution();
    }

    public void migrateSubtask(String targetHost, int taskId, int routeId)  throws InvalidRouteException, RoutingTypeNotSupportedException, HostNotExistException, TaskNotExistingException {
        String workerLogicalName = _slaveActor.getLogicalName();
        if(_bolts.containsKey(taskId) && _bolts.get(taskId).get_elasticTasks().get_routingTable().getRoutes().contains(routeId)) {
            if(!workerLogicalName.equals(targetHost)) {
                _slaveActor.sendMessageToMaster("Migration from local to remote!");
                migrateSubtaskToRemoteHost(targetHost, taskId, routeId);

            }
            else
                throw new RuntimeException("Cannot migrate " + taskId + "." + routeId + " on " + targetHost + ", because the subtask is already running on the host!");
        } else {
            if(workerLogicalName.equals(targetHost)) {
                _slaveActor.sendMessageToMaster("Migration from remote to local!");
                withdrawRemoteElasticTasks(taskId,routeId);

            } else {
                _slaveActor.sendMessageToMaster("Migration from remote to remote!");
                withdrawRemoteElasticTasks(taskId, routeId);
                migrateSubtaskToRemoteHost(targetHost, taskId, routeId);
            }
        }
    }
      
    public void migrateSubtaskToRemoteHost(String targetHost, int taskId, int routeId) throws InvalidRouteException, RoutingTypeNotSupportedException, HostNotExistException {
        ElasticTaskMigrationMessage migrationMessage = ElasticTaskHolder.instance().generateRemoteElasticTasks(taskId, routeId);
        SubtaskMigrationTimer.instance().prepare();
        routeIdToRemoteHost.put(new RouteId(taskId, routeId), targetHost);
        ElasticTaskMigrationConfirmMessage confirmMessage = (ElasticTaskMigrationConfirmMessage) _slaveActor.sendMessageToNodeAndWaitForResponse(targetHost, migrationMessage);
        if(confirmMessage != null)
            handleElasticTaskMigrationConfirmMessage(confirmMessage);
        else {
            sendMessageToMaster("Waiting for confirm message time out!");
        }
    }

    private void handleElasticTaskMigrationConfirmMessage(ElasticTaskMigrationConfirmMessage confirmMessage) {

        String ip = confirmMessage._ip;
        int port = confirmMessage._port;
        int taskId = confirmMessage._taskId;

        System.out.print("Received ElasticTaskMigrationConfirmMessage #. routes: "+confirmMessage._routes.size());
        for(int i: confirmMessage._routes) {
            establishConnectionToRemoteTaskHolder(taskId, i, ip, port);
        }
        SubtaskMigrationTimer.instance().launched();
        sendMessageToMaster("Task Migration completes!");
        sendMessageToMaster(SubtaskMigrationTimer.instance().toString());
    }


}
