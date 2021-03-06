package de.wieczorek.chart.advisor.types;

import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.nn.NeuralNetworkPathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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


        try {
            network.train(generator, 150);
        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }


}
