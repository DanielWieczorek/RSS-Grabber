package de.wieczorek.rss.trading.business;

import de.wieczorek.nn.PolicyDao;
import de.wieczorek.rss.trading.common.DataGenerator;
import de.wieczorek.rss.trading.types.StateEdge;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.primitives.Pair;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

@ApplicationScoped
public class Trainer {

    @Inject
    private DataGenerator dataGenerator;

    @Inject
    private PolicyDao dao;

    private static final AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration TRADE_QL = new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
            new Random(System.currentTimeMillis()).nextInt(), // Random seed
            10 * 7200, // Max step By epoch
            7200000, // Max step
            1, // size of batches
            360,
            720, // target update (hard)
            1000, // num step noop warmup
            0.01, // reward scaling
            0.8, // gamma
            100.0, // td-error clipping
            0.01f, // min epsilon
            36000 // num step for eps greedy anneal
    );

    private static final DQNFactoryStdDense.Configuration TRADE_NET = DQNFactoryStdDense.Configuration.builder()
            .l2(0.01).updater(new Adam(0.0005)).numLayer(3).numHiddenNodes(192)
            .listeners(new TrainingListener[]{new PerformanceListener(1, true)})
            .build();

    private DataManager manager;
    private MDP<NeuralNetworkState, Integer, DiscreteSpace> mdp;
    private Learning<NeuralNetworkState, Integer, DiscreteSpace, IDQN> dql;
    private static final String SAVE_FILE = System.getProperty("user.home")
            + "/neural-networks/trading-trading-data.zip";

    @PostConstruct
    public void init() {
        try {

            Nd4j.getMemoryManager().setAutoGcWindow(60 * 1000);

            manager = new DataManager();
            dataGenerator.loadData();
            mdp = new NeuralNetworkActor(360, dataGenerator.buildNewStartState(0), dataGenerator);

            Pair<IDQN, Object> data = null;
            if (Files.exists(new File(SAVE_FILE).toPath())) {
                data = DataManager.load(SAVE_FILE, Object.class);
            }
            if (data != null) {

                dql = new AsyncNStepQLearningDiscreteDense<>(mdp, data.getFirst(),
                        TRADE_QL, manager);
            } else {
                dql = new AsyncNStepQLearningDiscreteDense<>(mdp, TRADE_NET, TRADE_QL, manager);
            }
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO error handling
        }
    }

    public void train() throws IOException {
        dataGenerator.loadData();

        int rangeMax = dataGenerator.getMaxIndex() - (360);
        int numberOfRounds = rangeMax;

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < numberOfRounds; i++) {
            // TODO sort by number of trades and print it.

            StateEdge root = dataGenerator.buildNewStartState(random.nextInt(rangeMax));


            mdp = new NeuralNetworkActor(360, root, dataGenerator);

            // start the training
            dql.train();

            DQNPolicy<NeuralNetworkState> pol = (DQNPolicy<NeuralNetworkState>) dql.getPolicy();
            dao.writePolicy(pol);
            DataManager.save(SAVE_FILE, dql);
            dql = new AsyncNStepQLearningDiscreteDense<>(mdp,
                    DataManager.load(SAVE_FILE, Object.class).getFirst(),
                    TRADE_QL, manager);
            dql.setEpochCounter(0);
            dql.setStepCounter(0);

            System.out.println("running iteration " + i + " of " + numberOfRounds);
        }
    }


    @PreDestroy
    public void shutdown() {
        mdp.close();
    }

}
