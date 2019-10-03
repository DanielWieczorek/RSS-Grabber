package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.advisor.ui.Controller;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.persistence.EntityManagerContext;
import de.wieczorek.rss.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@EntityManagerContext
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
            logger.error("error while generating prediction: ", e);
        }
    }
}
