package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.StateEdge;

public class OracleInput {

    private StateEdge stateEdge;
    private TraderState state;

    public StateEdge getStateEdge() {
        return stateEdge;
    }

    public void setStateEdge(StateEdge stateEdge) {
        this.stateEdge = stateEdge;
    }

    public TraderState getState() {
        return state;
    }

    public void setState(TraderState state) {
        this.state = state;
    }
}
