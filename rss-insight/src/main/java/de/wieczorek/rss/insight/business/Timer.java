package de.wieczorek.rss.insight.business;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import de.wieczorek.rss.insight.ui.Controller;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class Timer implements Runnable {
    private static final Logger logger = LogManager.getLogger(Timer.class.getName());

    @Inject
    private SentimentAtTimeDao dao;

    @Inject
    private Controller controller;

    public Timer() {

    }

    @Override
    public void run() {
	try {

	    SentimentEvaluationResult result = controller.predict();
	    SentimentAtTime entity = new SentimentAtTime();
	    entity.setPositiveProbability(result.getSummary().getPositiveProbability());
	    entity.setNegativeProbability(result.getSummary().getNegativeProbability());
	    entity.setSentimentTime(LocalDateTime.now().withSecond(0).withNano(0));
	    if (dao.findById(entity.getSentimentTime()) == null) {
		dao.persist(entity);
	    }

	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	}
    }

}
