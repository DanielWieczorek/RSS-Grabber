package de.wieczorek.rss.trading.business;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;

import de.wieczorek.rss.trading.business.data.StateEdge;

public class NeuralNetworkActor implements MDP<NeuralNetworkState, Integer, DiscreteSpace> {

    final private int maxStep;

    private DiscreteSpace actionSpace = new DiscreteSpace(2); // 0 Buy, 1 Sell
    private ObservationSpace<NeuralNetworkState> observationSpace = new ArrayObservationSpace<>(new int[] { 24 });
    private NeuralNetworkState currentState;
    private IndexedStateGraph stateGraph;

    public NeuralNetworkActor(int maxStep, IndexedStateGraph stateGraph) {
	this.maxStep = maxStep;
	this.stateGraph = stateGraph;
	currentState = new NeuralNetworkState(stateGraph.getRootEdge(), 0);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isDone() {
	return currentState.getStep() == maxStep || currentState.getState().getActions().isEmpty();
    }

    @Override
    public NeuralNetworkState reset() {
	currentState = new NeuralNetworkState(stateGraph.getRootEdge(), 0);
	return currentState;
    }

    public NeuralNetworkState resetForNewTrainingData(IndexedStateGraph graph) {
	stateGraph = graph;
	return reset();
    }

    @Override
    public StepReply<NeuralNetworkState> step(Integer a) {
	StateEdge newState = currentState.getState().getActions().get(a).getTargetState();
	double reward = newState.getAccount().getEurEquivalent()// If number of trades < 2 ->
								// reward double min
		- currentState.getState().getAccount().getEurEquivalent();
	currentState = new NeuralNetworkState(newState, currentState.getStep() + 1);
	return new StepReply<>(currentState, reward, isDone(), new JSONObject("{}"));
    }

    @Override
    public NeuralNetworkActor newInstance() {
	NeuralNetworkActor simpleToy = new NeuralNetworkActor(maxStep, stateGraph);
	return simpleToy;
    }

    @Override
    public DiscreteSpace getActionSpace() {

	return actionSpace;
    }

    @Override
    public ObservationSpace<NeuralNetworkState> getObservationSpace() {
	return observationSpace;
    }

}
