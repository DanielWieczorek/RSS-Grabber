package de.wieczorek.chart.advisor.types;

import de.wieczorek.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingNeuralNetworkTrainer nn;

    @Inject
    private DataGenerator generator;

    @Override
    public void run() {
        try {
            nn.train(generator, 50);
        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }
}
