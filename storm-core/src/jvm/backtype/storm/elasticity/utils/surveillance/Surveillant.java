package backtype.storm.elasticity.utils.surveillance;

import backtype.storm.generated.SurveillanceService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 10/29/15.
 */
public class Surveillant implements SurveillanceService.Iface, Runnable {

    public static String thriftIp = backtype.storm.elasticity.config.Config.masterIp;

    public static int thriftPort = 22000;

    private Thread serverThread;

    private Map<String, Double> throughputs = new HashMap<String,Double>();

    public void start() {
        serverThread = new Thread(this);
        serverThread.start();
    }

    @Override
    public void reportExecutorThroughput(String executorId, double value) throws org.apache.thrift.TException {
        throughputs.put(executorId, value);
        throughputs.put(executorId, value);
    }

    private boolean startServer() {

        SurveillanceService.Processor processor = new SurveillanceService.Processor(this);

        try {
            TServerTransport serverTransport = new TServerSocket(this.thriftPort);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
       //     System.out.println("Hello");
            System.out.println("Server is working...");
       //     System.out.println("Hello");
            server.serve();

        }
        catch (TTransportException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {

        Surveillant server = new Surveillant();

        server.start();

        ThroughputDisplay throughputDisplay = new ThroughputDisplay(server);
        try {
            while(true) {
                Thread.sleep(1000);
           //     System.out.println("Hello");
                throughputDisplay.flushResult(System.out);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void run() {
        startServer();
    }

    public Map<String, Double> getThroughputs() {
        return throughputs;
    }
}
