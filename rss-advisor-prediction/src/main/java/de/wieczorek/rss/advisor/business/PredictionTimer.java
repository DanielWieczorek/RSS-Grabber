package de.wieczorek.rss.advisor.business;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.advisor.ui.Controller;

//@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class PredictionTimer implements Runnable {
    private static final Logger logger = LogManager.getLogger(PredictionTimer.class.getName());

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
