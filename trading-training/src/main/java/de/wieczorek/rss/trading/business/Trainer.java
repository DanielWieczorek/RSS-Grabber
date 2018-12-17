package de.wieczorek.rss.trading.business;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;

import de.wieczorek.nn.PolicyDao;
import de.wieczorek.rss.trading.business.data.StateEdge;

@ApplicationScoped
public class Trainer {

    @Inject
    private TrainingDataGenerator dataGenerator;

    @Inject
    private PolicyDao dao;

    private static final QLearning.QLConfiguration TRADE_QL = new QLearning.QLConfiguration(123, // Random seed
	    100000, // Max step By epoch
	    20000, // Max step
	    100000, // Max size of experience replay
	    256, // size of batches
	    100, // target update (hard)
	    0, // num step noop warmup
	    0.05, // reward scaling
	    0.99, // gamma
	    10.0, // td-error clipping
	    0.1f, // min epsilon
	    2000, // num step for eps greedy anneal
	    true // double DQN
    );

    private static final DQNFactoryStdDense.Configuration TRADE_NET = DQNFactoryStdDense.Configuration.builder()
	    .l2(0.01).updater(new Adam(1e-2)).numLayer(8).numHiddenNodes(128).build();

    private DataManager manager;
    private MDP<NeuralNetworkState, Integer, DiscreteSpace> mdp;
    private Learning<NeuralNetworkState, Integer, DiscreteSpace, IDQN> dql;

    @PostConstruct
    public void init() {
	try {
	    manager = new DataManager();
	    mdp = new NeuralNetworkActor(19, new IndexedStateGraph(dataGenerator.generateTrainingData()));
	    dql = new QLearningDiscreteDense<NeuralNetworkState>(mdp, TRADE_NET, TRADE_QL, manager);
	} catch (Exception e) {
	    throw new RuntimeException(e); // TODO error handling
	}
    }

    public void train() throws IOException {

	StateEdge root = dataGenerator.generateTrainingData();

	IndexedStateGraph graph = new IndexedStateGraph(root);
	((NeuralNetworkActor) mdp).resetForNewTrainingData(graph);

	// start the training
	dql.train();

	DQNPolicy<NeuralNetworkState> pol = (DQNPolicy<NeuralNetworkState>) dql.getPolicy();

	dao.writePolicy(pol);
	dql.setEpochCounter(0);
	dql.setStepCounter(0);

	printOptimumResult(graph);
    }

    private void printOptimumResult(IndexedStateGraph graph) {
	StateEdge bestTerminalState = graph.getTerminalStateWithHighestValue();
	System.out.println("the optimum is: " + (bestTerminalState.getAccount().getEurEquivalent()
		- graph.getRootEdge().getAccount().getEurEquivalent()));
    }

    @PreDestroy
    public void shutdown() {
	mdp.close();
    }

}
