package backtype.storm.elasticity.ActorFramework.Message;

/**
 * Created by Robert on 11/11/15.
 */
public class HelloMessage implements IMessage {

    String _name;

    public HelloMessage(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}
