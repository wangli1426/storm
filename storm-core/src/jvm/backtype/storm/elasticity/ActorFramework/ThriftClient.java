package backtype.storm.elasticity.ActorFramework;

import backtype.storm.generated.MasterService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by Robert on 11/13/15.
 */
public class ThriftClient {

    public static void main(String[] args) {
        TTransport transport = new TSocket("127.0.0.1",9090);
        try {
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);

            MasterService.Client thriftClient = new MasterService.Client(protocol);
            thriftClient.migrateTasks(args[0],args[1],Integer.parseInt(args[2]),Integer.parseInt(args[3]));
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
        System.out.println("finished!");
    }
}