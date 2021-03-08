package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.advisor.business.Controller;
import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.core.kafka.KafkaSender;
import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.rss.advisor.types.RssTradingEvaluationResultTopicConfiguration;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.insight.types.RssSentimentTopicConfiguration;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
@EntityManagerContext
@WithTopicConfiguration(configName = RssSentimentTopicConfiguration.class)
public class KafkaReceiver implements Consumer<SentimentEvaluationResult> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @Inject
    private Controller controller;

    @Inject
    private TradingEvaluationResultDao dao;

    @Inject
    @WithTopicConfiguration(configName = RssTradingEvaluationResultTopicConfiguration.class)
    private KafkaSender<Object> sender;

    @Override
    public void accept(SentimentEvaluationResult evaluationResult) {
        try {
            TradingEvaluationResult result = controller.predict();

            if (result != null && dao.findById(result.getCurrentTime(), result.getTargetTime()) == null) {
                dao.persist(result);


                sender.send(result.getCurrentTime().toString(), result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error while generating prediction: ", e);
        }
    }
}
