package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.advisor.business.Controller;
import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.core.business.kafka.ChartMetricTopicConfiguration;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.core.kafka.KafkaSender;
import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
@EntityManagerContext
@WithTopicConfiguration(configName = ChartMetricTopicConfiguration.class)
public class KafkaReceiver implements Consumer<ChartMetricRecord> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @Inject
    private Controller controller;

    @Inject
    private TradingEvaluationResultDao dao;

    @Inject
    @WithTopicConfiguration(configName = ChartAdvisorEvaluationResultTopicConfiguration.class)
    private KafkaSender<Object> sender;


    @Override
    public void accept(ChartMetricRecord foo) {
        try {
            TradingEvaluationResult result = controller.predict();

            if (result != null) {
                dao.upsert(result);
            }

            sender.send(result.getTargetTime().toString(), result);

        } catch (Exception e) {
            logger.error("error while generating prediction: ", e);

        }

    }


}
