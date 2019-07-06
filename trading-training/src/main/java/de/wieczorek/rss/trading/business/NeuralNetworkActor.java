package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.common.DataGenerator;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;

import de.wieczorek.rss.trading.types.StateEdge;

import java.util.UUID;

public class NeuralNetworkActor implements MDP<NeuralNetworkState, Integer, DiscreteSpace> {

    final private int maxStep;
    private final DataGenerator generator;

    private DiscreteSpace actionSpace = new DiscreteSpace(2); // 0 Buy, 1 Sell
    private ObservationSpace<NeuralNetworkState> observationSpace = new ArrayObservationSpace<>(new int[] { 102 });
    private NeuralNetworkState currentState;
    private StateEdge startingEdge;
    private Integer lastAction = -1 ;
    private StateEdge lastBuy;

    private UUID id = UUID.randomUUID();

    private int numberOfTrades = 0;
    private double totalProfit = 0;
    private double positiveTrades = 0;



    public NeuralNetworkActor(int maxStep, StateEdge currentEdge, DataGenerator generator) {
	this.maxStep = maxStep;
        this.startingEdge = currentEdge;
	currentState = new NeuralNetworkState(startingEdge, 0);
	this.generator = generator;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isDone() {
	return generator.buildNextState(currentState.getState(),0) == null ||currentState.getStep() == maxStep;
    }

    @Override
    public NeuralNetworkState reset() {
	currentState = new NeuralNetworkState(startingEdge, 0);
        lastBuy = null;
        numberOfTrades = 0;
        totalProfit = 0;positiveTrades = 0;

        return currentState;
    }


    @Override
    public StepReply<NeuralNetworkState> step(Integer a) {
        StateEdge newState = generator.buildNextState(currentState.getState(),a);

        if(a == 0 && (lastAction == 1 ||lastAction == -1)){ // buy
          //  System.out.println(id.toString()+": buying@"+newState.getAccount().getEurEquivalent()+" step: "+"("+currentState.getStep()+")");
            lastBuy =  newState;
        }
        lastAction = a;

	    double reward = 0.0;
	    if(a == 1 && lastBuy != null){
	        reward = newState.getAccount().getEurEquivalent() - lastBuy.getAccount().getEurEquivalent();
	        lastBuy = null;
            numberOfTrades ++;
            totalProfit += reward;
          //  System.out.println(id.toString()+":  selling@"+newState.getAccount().getEurEquivalent()+" reward: "+ reward +" step: "+"("+currentState.getStep()+")");
        }

     //   double reward = (newState.getAccount().getEurEquivalent()
     //           - currentState.getState().getAccount().getEurEquivalent());
        currentState = new NeuralNetworkState(newState, currentState.getStep() + 1);
        return new StepReply<>(currentState, isDone() && numberOfTrades == 0?0.0:reward, isDone(), new JSONObject("{}"));


    }

    @Override
    public NeuralNetworkActor newInstance() {
	NeuralNetworkActor simpleToy = new NeuralNetworkActor(maxStep, startingEdge, generator);
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
