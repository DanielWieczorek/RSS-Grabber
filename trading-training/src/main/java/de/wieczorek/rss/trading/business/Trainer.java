package de.wieczorek.rss.trading.business;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning.QLConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.primitives.Pair;

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
	    2000, // Max step
	    10000, // Max size of experience replay
	    64, // size of batches
	    100, // target update (hard)
	    0, // num step noop warmup
	    0.05, // reward scaling
	    0.99, // gamma
	    10.0, // td-error clipping
	    0.1f, // min epsilon
	    200, // num step for eps greedy anneal
	    true // double DQN
    );

    private static final DQNFactoryStdDense.Configuration TRADE_NET = DQNFactoryStdDense.Configuration.builder()
	    .l2(0.01).updater(new Adam(1e-2)).numLayer(3).numHiddenNodes(192).build();

    private DataManager manager;
    private MDP<NeuralNetworkState, Integer, DiscreteSpace> mdp;
    private Learning<NeuralNetworkState, Integer, DiscreteSpace, IDQN> dql;
    private static final String SAVE_FILE = System.getProperty("user.home")
	    + "/neural-networks/trading-trading-data.zip";

    @PostConstruct
    public void init() {
	try {
	    manager = new DataManager();
	    mdp = new NeuralNetworkActor(19, new IndexedStateGraph(dataGenerator.generateTrainingData(0)));

	    Pair<IDQN, QLConfiguration> data = null;
	    if (Files.exists(new File(SAVE_FILE).toPath())) {
		data = DataManager.load(SAVE_FILE, QLConfiguration.class);
	    }
	    if (data != null) {
		dql = new QLearningDiscreteDense<NeuralNetworkState>(mdp, data.getFirst(), data.getSecond(), manager);
	    } else {
		dql = new QLearningDiscreteDense<NeuralNetworkState>(mdp, TRADE_NET, TRADE_QL, manager);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e); // TODO error handling
	}
    }

    public void train() throws IOException {
	int numberOfRounds = 20;
	int rangeMax = dataGenerator.getMaxIndex() - 1440;
	Random random = new Random(System.currentTimeMillis());
	for (int i = 0; i < numberOfRounds; i++) {
	    StateEdge root = dataGenerator.generateTrainingData(random.nextInt(rangeMax));

	    IndexedStateGraph graph = new IndexedStateGraph(root);
	    ((NeuralNetworkActor) mdp).resetForNewTrainingData(graph);

	    // start the training
	    dql.train();

	    DQNPolicy<NeuralNetworkState> pol = (DQNPolicy<NeuralNetworkState>) dql.getPolicy();

	    dao.writePolicy(pol);
	    DataManager.save(SAVE_FILE, dql);
	    dql.setEpochCounter(0);
	    dql.setStepCounter(0);

	    printOptimumResult(graph);
	}
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
