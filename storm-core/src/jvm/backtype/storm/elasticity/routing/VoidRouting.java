package backtype.storm.elasticity.routing;

import java.util.ArrayList;

/**
 * Created by Robert on 11/4/15.
 */
public class VoidRouting implements RoutingTable {
    @Override
    public int route(Object key) {
        return origin;
    }

    @Override
    public int getNumberOfRoutes() {
        return 0;
    }

    @Override
    public ArrayList<Integer> getRoutes() {
        return null;
    }
}