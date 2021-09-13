package de.wieczorek.chart.advisor.types;

import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.nn.NeuralNetworkPathBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 30, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingNeuralNetworkTrainer network;

    @Inject
    private TrainingDataGenerator generator;

    @Inject
    private NeuralNetworkPathBuilder pathBuilder;

    @Override
    public void run() {
        MultiLayerNetwork net = readNetwork();
        List<TrainingNetInputItem> input = generator.generate();


        try {
            network.train(generator, 10);
        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }


    private MultiLayerNetwork readNetwork() {
        File dir = pathBuilder.getCheckpointsPath();
        MultiLayerNetwork net = null;
        File[] checkpointFiles = dir.listFiles();
        if (checkpointFiles != null
                && checkpointFiles.length > 0
                && CheckpointListener.lastCheckpoint(dir) != null) {
            net = CheckpointListener.loadCheckpointMLN(dir, CheckpointListener.lastCheckpoint(dir));
        }
        return net;
    }

}
