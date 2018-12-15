package de.wieczorek.rss.trading.business;

import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;

import de.wieczorek.rss.trading.ui.StateEdge;
import de.wieczorek.rss.trading.ui.StateEdgePart;

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

	List<StateEdgePart> parts = state.getAllStateParts().subList(state.getPartsStartIndex(),
		state.getPartsEndIndex());
	double[] result = new double[10 * parts.size()];

	// Check if at least 2 parts
	for (int i = 0; i < parts.size(); i++) {
	    StateEdgePart currentPart = parts.get(i);
	    result[8 * i + 0] = 0.0;// currentPart.getSentiment().getPredictedDelta();

	    result[8 * i + 1] = currentPart.getChartEntry().getOpen();
	    result[8 * i + 2] = currentPart.getChartEntry().getHigh();
	    result[8 * i + 3] = currentPart.getChartEntry().getLow();
	    result[8 * i + 4] = currentPart.getChartEntry().getClose();
	    result[8 * i + 5] = currentPart.getChartEntry().getVolume();
	    result[8 * i + 6] = currentPart.getChartEntry().getVolumeWeightedAverage();
	    result[8 * i + 7] = currentPart.getChartEntry().getTransactions();

	    result[8 * i + 8] = state.getAccount().getBtc() > 0.0 ? 1.0 : 0.0;
	    result[8 * i + 9] = state.getAccount().getEur() > 0.0 ? 1.0 : 0.0;
	}
	return result;
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