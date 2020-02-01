package de.wieczorek.rss.insight.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 10, unit = TimeUnit.SECONDS) // TODO revert
@EntityManagerContext
@ApplicationScoped
public class PredictionTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PredictionTimer.class);

    @Inject
    private SentimentAtTimeDao dao;

    @Inject
    private Controller controller;


    @Override
    public void run() {
        try {
            logger.info("predicting");
            SentimentEvaluationResult result = controller.predict();
            SentimentAtTime entity = new SentimentAtTime();
            entity.setPositiveProbability(result.getSummary().getPositiveProbability());
            entity.setNegativeProbability(result.getSummary().getNegativeProbability());
            entity.setSentimentTime(LocalDateTime.now().withSecond(0).withNano(0));
            if (dao.findById(entity.getSentimentTime()) == null) {
                dao.persist(entity);
            }

        } catch (Exception e) {
            logger.error("error while generating prediction: ", e);
        }
    }

}
