package storm.starter.elasticity;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.starter.elasticity.util.StateConsistencyValidator;
import storm.starter.util.KeyGenerator;
import storm.starter.util.RoundRobinKeyGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by robert on 22/12/16.
 */
public class IncrementalValidatorSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private Map<Integer, Long> keyToCount;
    private int numberOfKeys;
    private Random random;
    private boolean acked;
    private KeyGenerator generator;
    public IncrementalValidatorSpout(int numberOfKeys, boolean acked) {
        this.numberOfKeys = numberOfKeys;
        this.acked = acked;
        random = new Random();
    }


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        keyToCount = new HashMap<>();
        for(int i = 0; i < numberOfKeys; i++) {
            keyToCount.put(i, 0L);
        }
        generator = new RoundRobinKeyGenerator(numberOfKeys);
    }

    @Override
    public void nextTuple() {
        final Integer key = generator.generate();
        final Long count = keyToCount.get(key);
        keyToCount.put(key, count + 1);
        if (acked)
            collector.emit(new Values(key, count), new Object());
        else
            collector.emit(new Values(key, count));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key", "count"));
    }
}
