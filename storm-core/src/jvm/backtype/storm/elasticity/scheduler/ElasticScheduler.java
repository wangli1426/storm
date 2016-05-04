package backtype.storm.elasticity.scheduler;

import backtype.storm.elasticity.actors.Master;
import backtype.storm.elasticity.actors.Slave;
import backtype.storm.elasticity.common.RouteId;
import backtype.storm.elasticity.config.Config;
import backtype.storm.elasticity.exceptions.RoutingTypeNotSupportedException;
import backtype.storm.elasticity.message.actormessage.ExecutorScalingInRequestMessage;
import backtype.storm.elasticity.message.actormessage.ExecutorScalingOutRequestMessage;
import backtype.storm.elasticity.message.actormessage.ScalingInSubtaskCommand;
import backtype.storm.elasticity.resource.ResourceManager;
import backtype.storm.elasticity.routing.BalancedHashRouting;
import backtype.storm.elasticity.routing.RoutingTable;
import backtype.storm.elasticity.routing.RoutingTableUtils;
import backtype.storm.elasticity.utils.FirstFitDoubleDecreasing;
import backtype.storm.elasticity.utils.Histograms;
import backtype.storm.elasticity.utils.PartitioningMinimizedMovement;
import backtype.storm.elasticity.utils.timer.SmartTimer;
import backtype.storm.generated.TaskNotExistException;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.ArrayQueue;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Robert on 11/11/15.
 */
public class ElasticScheduler {

    Master master;

    ResourceManager resourceManager;

    final Object lock = new Object();

    static private ElasticScheduler instance;

    LinkedBlockingQueue<Object> scalingRequestQueue = new LinkedBlockingQueue<>();

    LinkedBlockingQueue<Integer> pendingTaskLevelLoadBalancingQueue = new LinkedBlockingQueue<>();

    public ElasticScheduler() {
        master = Master.createActor();
        resourceManager = new ResourceManager();
        instance = this;

        if(Config.EnableWorkerLevelLoadBalancing) {
            enableWorkerLevelLoadBalancing();
        }

        if(Config.EnableSubtaskLevelLoadBalancing) {
            enableSubtaskLevelLoadBalancing();
        }

        if(Config.EnableAutomaticScaling) {
            enableAutomaticScaling();
        }


    }

    static public ElasticScheduler getInstance() {
        return instance;
    }

    private void enableWorkerLevelLoadBalancing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(Config.WorkerLevelLoadBalancingCycleInSecs * 1000);
                        synchronized (lock) {
                            Set<Integer> taskIds = master._elasticTaskIdToWorkerLogicalName.keySet();
                            for(Integer task: taskIds) {
                                try {
//                                    workerLevelLoadBalancing(task);
                                } catch (Exception e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void enableSubtaskLevelLoadBalancing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(Config.SubtaskLevelLoadBalancingCycleInSecs * 1000);
                        Set<Integer> taskIds = master._elasticTaskIdToWorkerLogicalName.keySet();
                        for(Integer task: taskIds) {
                            try {
                                pendingTaskLevelLoadBalancingQueue.offer(task);
                            } catch (Exception e ) {
                                e.printStackTrace();
                            }
                        }


                    }
                } catch (InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        int taskId = pendingTaskLevelLoadBalancingQueue.take();
                        synchronized (lock) {
                            long start = System.currentTimeMillis();
                            optimizeBucketToRoutingMapping(taskId);
                            System.out.println("Load-balancing in " + (System.currentTimeMillis() - start)/1000.0 + " s");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void enableAutomaticScaling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {

                        Object request = scalingRequestQueue.take();
                        System.out.println("Received one request! " + scalingRequestQueue.size() + " pending");
                        if(request instanceof ExecutorScalingInRequestMessage) {
                            System.out.println("Handling Scaling in request!");
                            ExecutorScalingInRequestMessage requestMessage = (ExecutorScalingInRequestMessage)request;
                            final boolean skewed = isWorkloadSkewed(requestMessage.taskID);
                            if(skewed) {
                                System.out.println("Scaling in request on " + requestMessage.taskID + " is ignored, as skewness is detected!");
                                pendingTaskLevelLoadBalancingQueue.add(requestMessage.taskID);
                            } else
                                synchronized (lock) {
                                    System.out.println("Scaling in command will be called!");
                                    long start = System.currentTimeMillis();
                                    master.handleExecutorScalingInRequest(requestMessage.taskID);
                                    System.out.println("Scaling in " + (System.currentTimeMillis() - start)/1000.0 + " s");
                                }
                        }
                        if(request instanceof ExecutorScalingOutRequestMessage) {
                            System.out.println("Scaling out!");
                            ExecutorScalingOutRequestMessage requestMessage = (ExecutorScalingOutRequestMessage)request;
                            synchronized (lock) {
                                long start = System.currentTimeMillis();
                                master.handleExecutorScalingOutRequest(requestMessage.taskId);
                                System.out.println("Scaling out " + (System.currentTimeMillis() - start)/1000.0 + " s");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String optimizeBucketToRoutingMapping(int taskId) throws TaskNotExistException, RoutingTypeNotSupportedException, TException {
        return optimizeBucketToRoutingMapping(taskId, Config.taskLevelLoadBalancingThreshold);
    }

    public void addScalingRequest(Object request) {
        scalingRequestQueue.offer(request);
    }


    public double getSkewnessFactor(Histograms histograms, BalancedHashRouting balancedHashRouting) throws TaskNotExistException, RoutingTypeNotSupportedException {
//        RoutingTable routingTable = master.getRoutingTable(taskId);
//        BalancedHashRouting balancedHashRouting = RoutingTableUtils.getBalancecHashRouting(routingTable);
//        if(balancedHashRouting == null) {
//            throw new RoutingTypeNotSupportedException("Only support BalancedHashRouting family routing table!");
//        }
//            System.out.println("routing Table: " + balancedHashRouting.toString());



        // 2. get Distribution;
//        Histograms histograms = master.getBucketDistribution(taskId);
//            System.out.println("Histograms: " + histograms.toString());

        // 3. evaluate the skewness
        Map<Integer, Integer> shardToRouteMapping = balancedHashRouting.getBucketToRouteMapping();
        final int numberOfRoutes = balancedHashRouting.getNumberOfRoutes();
        long[] routeLoads = new long[numberOfRoutes];

        for(Integer shard: shardToRouteMapping.keySet()) {
            //            System.out.println("\n\n shard:" + shard);
            //            System.out.println("numberOfRoutes: " + numberOfRoutes);
            //            System.out.println("shardToRouteMapping.get(shard): "+ shardToRouteMapping.get(shard) + "\n");
            //            System.out.println("routeLoads[shardToRouteMapping.get(shard)]" + routeLoads[shardToRouteMapping.get(shard)] + "\n");
            //            System.out.println("histograms.histogramsToArrayList().get(shard)" + histograms.histogramsToArrayList().get(shard) + "\n");
            routeLoads[shardToRouteMapping.get(shard)] += histograms.histogramsToArrayList().get(shard);
        }

        long loadSum = 0;
        long loadMin = Long.MAX_VALUE;
        long loadMax = Long.MIN_VALUE;
        for(Long i: routeLoads) {
            loadSum += i;
        }
        for(Long i: routeLoads) {
            if(loadMin > i){
                loadMin = i;
            }
        }
        for(Long i: routeLoads) {
            if(loadMax < i) {
                loadMax = i;
            }
        }

        double averageLoad = loadSum / (double)numberOfRoutes;
//            boolean skewness = (loadMax - averageLoad)/averageLoad > 0.8;

        return (loadMax - loadMin) / (double)loadMax;
    }

    public boolean isWorkloadSkewed(int taskId) {
        try {
            RoutingTable routingTable = master.getRoutingTable(taskId);
            BalancedHashRouting balancedHashRouting = RoutingTableUtils.getBalancecHashRouting(routingTable);
            if(balancedHashRouting == null) {
                throw new RoutingTypeNotSupportedException("Only support BalancedHashRouting family routing table!");
            }

            // 2. get Distribution;
            Histograms histograms = master.getBucketDistribution(taskId);

            double workloadFactor = getSkewnessFactor(histograms, balancedHashRouting);
            return workloadFactor >= Config.taskLevelLoadBalancingThreshold;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String optimizeBucketToRoutingMapping(int taskId, double threshold) throws TaskNotExistException, RoutingTypeNotSupportedException, TException {

        synchronized (lock) {
            System.out.println("Task-level Load Balance model is called on Executor " + taskId);
            // 1. get routingTable
            RoutingTable routingTable = master.getRoutingTable(taskId);
            BalancedHashRouting balancedHashRouting = RoutingTableUtils.getBalancecHashRouting(routingTable);
            if(balancedHashRouting == null) {
//                throw new RoutingTypeNotSupportedException("Only support BalancedHashRouting family routing table!");
                System.out.println("Only support BalancedHashRouting family routing table!");
                return "Only support BalancedHashRouting family routing table!";
            }

            // 2. get Distribution;
            Histograms histograms = master.getBucketDistribution(taskId);

            System.out.println(histograms);

            double workloadFactor = getSkewnessFactor(histograms, balancedHashRouting);
            boolean skewness = workloadFactor >= threshold;

//            System.out.println("Workload distribution:\n");
//            for(int i = 0; i < routeLoads.length; i++ ){
//                System.out.println(i + ": " + routeLoads[i]);
//            }
            System.out.print("Workload factor: " + workloadFactor);
            System.out.println("  Threshold: " + threshold);

            if(workloadFactor > 0.99) {
                System.out.println(histograms);
                System.out.println(master.queryDistribution(taskId));
            }

            if(skewness) {

//                ShardReassignmentPlan plan = getCompleteShardToTaskMapping(taskId, histograms, numberOfRoutes, balancedHashRouting.getBucketToRouteMapping());
                try {
                    ShardReassignmentPlan plan = getMinimizedShardToTaskReassignment(taskId, routingTable.getNumberOfRoutes(), balancedHashRouting.getBucketToRouteMapping(), histograms);

                    if(!plan.getReassignmentList().isEmpty()) {
                        applyShardToRouteReassignment(plan);
                    } else {
                        System.out.println("Shard Assignment is not modified after optimization.");
                    }
                    System.out.println(plan.getReassignmentList().size() + " shard reassignments has be performed for load balancing! ");
                    return plan.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Mapping: " + balancedHashRouting.getBucketToRouteMapping());
                    System.out.println("# of Routes: " + routingTable.getNumberOfRoutes());

                    Slave.getInstance().sendMessageToMaster(e.getMessage());
                    Slave.getInstance().sendMessageToMaster("Mapping: " + balancedHashRouting.getBucketToRouteMapping());
                    Slave.getInstance().sendMessageToMaster("# of Routes: " + routingTable.getNumberOfRoutes());
                    return "ERROR!!!";
                }
            } else {
                System.out.println("No shard reassignment will be performed!");
                return "The workload is not skewed!";
            }
        }

    }

    ShardReassignmentPlan getMinimizedShardToTaskReassignment(int taskId, int numberOfRoutes, Map<Integer, Integer> oldMapping,  Histograms histograms) {
        ShardReassignmentPlan plan = new ShardReassignmentPlan();

        PartitioningMinimizedMovement solver = new PartitioningMinimizedMovement(numberOfRoutes, oldMapping, histograms.histograms );

        Map<Integer, Integer> optimziedShardToTaskMapping = solver.getSolution();

        for(int shardID: optimziedShardToTaskMapping.keySet()){
            final int newTask = optimziedShardToTaskMapping.get(shardID);
            final int oldTask = oldMapping.get(shardID);
            if(newTask!=oldTask) {
                plan.addReassignment(taskId,shardID,oldTask,newTask);
//                System.out.println("Move " + shardID + " from " + oldTask + " to " + newTask + "\n");
            }
        }

        return plan;
    }

    ShardReassignmentPlan getCompleteShardToTaskMapping(int taskId, Histograms histograms, int numberOfRoutes, Map<Integer, Integer> oldMapping ){


        FirstFitDoubleDecreasing binPackingSolver = new FirstFitDoubleDecreasing(histograms.histogramsToArrayList(), numberOfRoutes);
        if(binPackingSolver.getResult() != numberOfRoutes) {
            System.out.println("Fail to solve the bin packing problem!");
            return null;
        }
        System.out.println(binPackingSolver.toString());


        Map<Integer, Integer> newMapping = binPackingSolver.getBucketToPartitionMap();
        ShardReassignmentPlan plan = new ShardReassignmentPlan();

        for(Integer bucket: oldMapping.keySet()) {
            if(!oldMapping.get(bucket).equals(newMapping.get(bucket)) ) {
                int oldRoute = oldMapping.get(bucket);
                int newRoute = newMapping.get(bucket);
                plan.addReassignment(taskId, bucket, oldRoute, newRoute);
//                System.out.println("Move " + bucket + " from " + oldRoute + " to " + newRoute + "\n");
            }
        }


        return plan;
    }

    void applyShardToRouteReassignment(ShardReassignmentPlan plan) throws TException{
        int totalMovements = plan.getReassignmentList().size();
        int i = 0;
        for(ShardReassignment reassignment: plan.getReassignmentList()) {
//            System.out.println("\n===================START========================");
//            System.out.println("Begin to conduct the " + i++ + "th movements, " + totalMovements + " in total!");
            String from = master.getRouteHosterName(reassignment.taskId, reassignment.originalRoute);
            String to = master.getRouteHosterName(reassignment.taskId, reassignment.newRoute);
//            System.out.println("Movement: " + reassignment.toString());
//            System.out.println("From " + from + " to " + to);
            master.reassignBucketToRoute(reassignment.taskId, reassignment.shardId, reassignment.originalRoute, reassignment.newRoute);
//            System.out.println("=====================END========================\n");
        }
    }

    void applySubtaskReassignmentPlan(SubtaskReassignmentPlan plan) throws TException {
        int totalMovements = plan.getSubTaskReassignments().size();
        int i = 0;
        for(SubtaskReassignment reassignment: plan.getSubTaskReassignments()) {
//            System.out.println("\n===================START========================");
//            System.out.println("Begin to conduct the " + i++ + "th movements, " + totalMovements + " in total!");
//            System.out.println("Move " + reassignment.taskId + "." + reassignment.routeId + " from " + reassignment.originalHost + " to " + reassignment.targetHost);
            master.migrateTasks(reassignment.originalHost, reassignment.targetHost, reassignment.taskId, reassignment.routeId);
//            System.out.println("=====================END========================\n");
        }
        System.out.println(totalMovements + " subtask movements are completed!");
    }

    public String naiveWorkerLevelLoadBalancing(int taskId) throws TException {
        SubtaskReassignmentPlan plan = new SubtaskReassignmentPlan();

        synchronized (lock) {
            ArrayList<String> workers = new ArrayList<>();
            workers.addAll(resourceManager.systemCPULoad.keySet());
            int workerIndex = 0;
            Map<String, String> taskIdRouteToWorkers = master._taskidRouteToWorker;
            for(String xdy: taskIdRouteToWorkers.keySet()) {
                if(!taskIdRouteToWorkers.get(xdy).equals(workers.get(workerIndex))) {
                    RouteId routeId = new RouteId(xdy);
                    plan.addSubtaskReassignment(taskIdRouteToWorkers.get(xdy), workers.get(workerIndex), routeId.TaskId, routeId.Route);
                }
                workerIndex = (workerIndex + 1) % workers.size();
            }
            applySubtaskReassignmentPlan(plan);
        }

        return plan.toString();
    }

    public String workerLevelLoadBalancing(int taskId) throws TException {
        if(!master._elasticTaskIdToWorkerLogicalName.containsKey(taskId))
            throw new TaskNotExistException("Task " + taskId + " does not exists!");

        Map<String, Double> workerLoad = resourceManager.getWorkerCPULoadCopy();
        Map<String, String> taskIdRouteToWorkers = master._taskidRouteToWorker;

        SubtaskReassignmentPlan totalPlan = new SubtaskReassignmentPlan();

        synchronized (lock) {
            for(String worker: workerLoad.keySet()) {
                if(workerLoad.get(worker) >= Config.WorkloadHighWaterMark) {
                    SubtaskReassignmentPlan localPlan = releaseLoadOnWorker(taskId, worker, workerLoad, taskIdRouteToWorkers);
                    totalPlan.concat(localPlan);
    //                totalPlan.con
                }
            }

            System.out.println("Load balancing plan: " + totalPlan);
            applySubtaskReassignmentPlan(totalPlan);


            Map<String, ArrayList<String>> workerToSubtaskRoute = new HashMap<>();
            for(String xdy: taskIdRouteToWorkers.keySet()) {
                String worker = taskIdRouteToWorkers.get(xdy);
                if(!workerToSubtaskRoute.containsKey(worker)) {
                    workerToSubtaskRoute.put(worker, new ArrayList<String>());
                }
                workerToSubtaskRoute.get(worker).add(xdy);
            }
            System.out.println("Existing assignment:");
            for(String worker: workerToSubtaskRoute.keySet()) {
                String str = "";
                str += worker + ": ";
                for(String xdy: workerToSubtaskRoute.get(worker)) {
                    str += xdy + " ";
                }
                System.out.println(str);
            }
        }
        return totalPlan.toString();
    }

    SubtaskReassignmentPlan releaseLoadOnWorker(int taskId, String worker, Map<String, Double> workerLoad, Map<String, String> taskIdRouteToWorkers) {

        SubtaskReassignmentPlan plan = new SubtaskReassignmentPlan();

        String hostWorker = master._elasticTaskIdToWorkerLogicalName.get(taskId);

        Map<String, Queue<String>> workerToTaskRoutes = new HashMap<>();

        for(String taskIdRoute: taskIdRouteToWorkers.keySet()) {
            String w = taskIdRouteToWorkers.get(taskIdRoute);
            if(!workerToTaskRoutes.containsKey(w)) {
                workerToTaskRoutes.put(w, new ArrayQueue<String>());
            }
            workerToTaskRoutes.get(w).add(taskIdRoute);
        }

        int movements = (int) Math.ceil(workerLoad.get(worker) - Config.WorkloadHighWaterMark);


        //first try to move subtask to the original host
        if(!hostWorker.equals(worker) && workerLoad.get(hostWorker)<=Config.WorkloadLowWaterMark) {
            while(workerLoad.get(worker) > Config.WorkloadHighWaterMark // target worker is overloaded
                    && workerLoad.get(hostWorker) + 1 <= Config.WorkloadHighWaterMark // hostWorker is idle
                    && workerToTaskRoutes.get(worker).size() > 0 //there are subtasks on the target worker
                    ) {
                String taskIdRoute = workerToTaskRoutes.get(worker).poll();
                int tid = Integer.parseInt(taskIdRoute.split(".")[0]);
                int route = Integer.parseInt(taskIdRoute.split(".")[1]);
                assert(tid == taskId);
                plan.addSubtaskReassignment(worker, hostWorker, tid, route);
                workerLoad.put(worker, workerLoad.get(worker) - 1);
                workerLoad.put(hostWorker, workerLoad.get(hostWorker) + 1);
            }

        }

        for(String w: workerLoad.keySet()) {
            while(workerLoad.get(worker) > Config.WorkloadHighWaterMark
                    && workerLoad.get(w) + 1 <= Config.WorkloadHighWaterMark
                    && workerToTaskRoutes.get(worker).size() > 0
                    ) {
                String taskIdRoute = workerToTaskRoutes.get(worker).poll();
                System.out.println("taskIdRoute: " + taskIdRoute);
                int tid = Integer.parseInt(taskIdRoute.split("\\.")[0]);
                int route = Integer.parseInt(taskIdRoute.split("\\.")[1]);
                assert(tid == taskId);
                plan.addSubtaskReassignment(worker, w, tid, route);
                workerLoad.put(worker, workerLoad.get(worker) - 1);
                workerLoad.put(w, workerLoad.get(w) + 1);
            }
        }

        return plan;
    }



}
