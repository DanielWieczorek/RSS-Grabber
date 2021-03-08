package de.wieczorek.rss.trading.business;

import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.rss.advisor.types.RssTradingEvaluationResultTopicConfiguration;
import de.wieczorek.rss.insight.types.RssSentimentTopicConfiguration;
import de.wieczorek.rss.trading.common.oracle.AveragesCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
@EntityManagerContext
@WithTopicConfiguration(configName = RssSentimentTopicConfiguration.class)
public class RssSentimentKafkaReceiver implements Consumer<RssTradingEvaluationResultTopicConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(RssSentimentKafkaReceiver.class);

    @Inject
    private Controller controller;

    @Override
    public void accept(RssTradingEvaluationResultTopicConfiguration evaluationResult) {
        try {
            AveragesCache.INSTANCE.invalidate();
            logger.debug("Triggering trading.");
            controller.triggerTrading();
        } catch (Exception e) {
            logger.error("error while triggering trading: ", e);
        }
    }
}
