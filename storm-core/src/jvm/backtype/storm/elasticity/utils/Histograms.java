package backtype.storm.elasticity.utils;

import backtype.storm.utils.Utils;
import scala.Array;

import java.io.Serializable;
import java.util.*;

/**
 * Created by robert on 12/18/15.
 * This class is used to represent a distribution on the key space
 */
public class Histograms implements Serializable {

    private Map<Integer, Long> histograms;

    public Histograms(Map<Integer, Long> histograms) {
        this.histograms = new HashMap<>();
        this.histograms.putAll(histograms);
//        this.histograms = histograms;
    }

    public String toString() {
        String ret = "Distribution: \n";
//        for(Object o: histograms.keySet()) {
//            ret += o + ": " + histograms.get(o) + " ";
//        }

        Object[] keys = histograms.keySet().toArray();
        Arrays.sort(keys);
        for(Object key: keys) {
            ret += key + ": " + histograms.get(key) + " ";
        }
        ret += "\n";
        return ret;
    }

    public void setDefaultValueForAbsentKey(int numberOfKeys) {
        for(int i=0; i< numberOfKeys; i++ ) {
            if(!histograms.containsKey(i)) {
                histograms.put(i, 0L );
            }
        }
    }

    public ArrayList<Long> histogramsToArrayList () {
        ArrayList<Long> ret = new ArrayList<Long>();
        Object[] keys = histograms.keySet().toArray();
        Arrays.sort(keys);
        for(Object key: keys) {
            ret.add(histograms.get(key));
        }

        return ret;
    }

}