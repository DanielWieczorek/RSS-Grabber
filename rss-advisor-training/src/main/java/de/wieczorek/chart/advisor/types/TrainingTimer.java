package de.wieczorek.chart.advisor.types;

import de.wieczorek.rss.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 0, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingNeuralNetworkTrainer network;

    @Inject
    private DataGenerator generator;

    public TrainingTimer() {

    }

    @Override
    public void run() {
        try {


            network.train(generator, 5);


        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }
}
