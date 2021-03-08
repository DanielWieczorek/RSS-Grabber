package de.wieczorek.rss.trading.business;

import de.wieczorek.chart.advisor.types.ChartAdvisorEvaluationResultTopicConfiguration;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.rss.trading.common.oracle.AveragesCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
@EntityManagerContext
@WithTopicConfiguration(configName = ChartAdvisorEvaluationResultTopicConfiguration.class)
public class ChartAdvisorKafkaReceiver implements Consumer<TradingEvaluationResult> {

    private static final Logger logger = LoggerFactory.getLogger(ChartAdvisorKafkaReceiver.class);

    @Inject
    private Controller controller;

    @Override
    public void accept(TradingEvaluationResult evaluationResult) {
        try {
            AveragesCache.INSTANCE.invalidate();
            logger.debug("Triggering trading.");
            controller.triggerTrading();
        } catch (Exception e) {
            logger.error("error while triggering trading: ", e);
        }
    }
}
