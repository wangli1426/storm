package backtype.storm.elasticity;

import backtype.storm.tuple.Tuple;
import org.joda.time.Seconds;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Robert on 11/4/15.
 */
public class QueryRunnable implements Runnable {

    BaseElasticBolt _bolt;

    private boolean _terminationRequest = false;

    private LinkedBlockingQueue<Tuple> _pendingTuples;

    private ElasticOutputCollector _outputCollector;

    private boolean interrupted = false;

    private int id;

    public QueryRunnable(BaseElasticBolt bolt, LinkedBlockingQueue<Tuple> pendingTuples, ElasticOutputCollector outputCollector, int id) {
        _bolt = bolt;
        _pendingTuples = pendingTuples;
        _outputCollector = outputCollector;
        this.id = id;
    }

    /**
     * Call this function to terminate the query thread.
     * This function returns when it guarantees that the thread has already processed
     * all the tuples balls the pending queue and terminated.
     * Note that before calling this function, you should guarantee that the pending queue
     * will no longer be inserted new tuples.
     */
    public void terminate() {
        _terminationRequest = true;
        try {
            while (!interrupted) {
//                System.out.println("Waiting for the termination of the worker thread...");
                System.out.println(_pendingTuples.size()+" elements remaining in the pending list!");
                Thread.sleep(1);
            }
            System.out.println("**********Query Runnable (" + id + ") is terminated!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!_terminationRequest || !_pendingTuples.isEmpty()) {
                Tuple input = _pendingTuples.poll(5, TimeUnit.MILLISECONDS);
                if(input!=null) {
                    _bolt.execute(input, _outputCollector);
                }
            }
            interrupted = true;

        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception ee) {
            System.err.print("Something is wrong in the query thread!");
            ee.printStackTrace();
        }
        System.out.println("A query thread is terminated!");
        ElasticTaskHolder.instance().sendMessageToMaster("**********Query Runnable (" + id + ") is terminated!");

    }
}