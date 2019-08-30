package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.common.NetworkInputBuilder;
import de.wieczorek.rss.trading.types.StateEdge;
import org.deeplearning4j.rl4j.space.Encodable;

public class NeuralNetworkState implements Encodable {

    private StateEdge state;
    private int step;

    public NeuralNetworkState(StateEdge state, int step) {
        super();
        this.state = state;
        this.step = step;
    }

    @Override
    public double[] toArray() {
        return NetworkInputBuilder.buildInputArray(state, state.getAccount());
    }

    public StateEdge getState() {
        return state;
    }

    public void setState(StateEdge state) {
        this.state = state;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

}