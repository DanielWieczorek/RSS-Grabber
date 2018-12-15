package de.wieczorek.rss.trading.business;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LogManager.getLogger(TrainingTimer.class.getName());

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
