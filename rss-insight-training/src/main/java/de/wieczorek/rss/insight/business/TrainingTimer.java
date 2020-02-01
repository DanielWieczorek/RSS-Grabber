package de.wieczorek.rss.insight.business;

import de.wieczorek.core.timer.RecurrentTask;
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
    private RssSentimentNeuralNetworkTrainer network;

    @Inject
    private RssWord2VecNetwork vec;

    @Inject
    private DataGenerator generator;

    @Override
    public void run() {
        try {

            logger.info("get all classified");


            vec.train(generator.generate());
            network.train(generator, 50000);
        } catch (Exception e) {
            logger.error("error while training network: ", e);
        }
    }

}
