package backtype.storm.elasticity.actors.utils;

import backtype.storm.generated.MasterService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by Robert on 11/20/15.
 */
public class QueryThroughput {

    public static void main(String[] args) {

        if(args.length!=2) {
            System.out.println("args: taskid, cycle-in-secs");
            return;
        }

        int taskid = Integer.parseInt(args[0]);
        TTransport transport = new TSocket("127.0.0.1",9090);
        try {
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            MasterService.Client thriftClient = new MasterService.Client(protocol);

            int i = 0;
            while(true) {
                Thread.sleep(1000*Integer.parseInt(args[1]));
                try{

                double throughput = thriftClient.reportTaskThroughput(taskid);
                System.out.println("Task "+ taskid + ": "+throughput);

                    if (i%5==0) {
                        System.out.println(thriftClient.queryDistribution(taskid));
                    }

                } catch (TException e ) {
                    e.printStackTrace();
                }
                i++;
            }

//            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("finished!");
    }
}