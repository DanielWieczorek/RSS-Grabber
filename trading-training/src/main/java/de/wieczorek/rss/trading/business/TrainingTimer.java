package de.wieczorek.rss.trading.business;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private Trainer trainer;

    public TrainingTimer() {

    }

    @Override
    public void run() {
	try {
	    trainer.train();
	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	}
    }
}
