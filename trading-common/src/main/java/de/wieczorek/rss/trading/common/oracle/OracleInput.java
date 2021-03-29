package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.StateEdge;

public class OracleInput {

    private StateEdge stateEdge;

    private double minOrder = 0.0001;

    public double getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(double minOrder) {
        this.minOrder = minOrder;
    }

    public StateEdge getStateEdge() {
        return stateEdge;
    }

    public void setStateEdge(StateEdge stateEdge) {
        this.stateEdge = stateEdge;
    }
}
