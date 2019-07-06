package de.wieczorek.rss.advisor.types;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.rss.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.rss.advisor.ui.Controller;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class PredictionTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PredictionTimer.class);

    @Inject
    private Controller controller;

    @Inject
    private TradingEvaluationResultDao dao;

    public PredictionTimer() {

    }

    @Override
    public void run() {
	try {
	    TradingEvaluationResult result = controller.predict();

	    if (result != null && dao.findById(result.getCurrentTime(), result.getTargetTime()) == null) {
		dao.persist(result);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error("error while retrieving chart data: ", e);
	}
    }
}
