package de.wieczorek.chart.advisor.business;

import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.advisor.types.DataPreparator;
import de.wieczorek.chart.advisor.types.NetInputItem;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.advisor.types.TradingNeuralNetworkPredictor;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionRemoteRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.ChartMetricRemoteRestCaller;
import de.wieczorek.core.kafka.KafkaTopicSubscriberManager;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private TradingNeuralNetworkPredictor nn;

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private TradingEvaluationResultDao dao;

    @Inject
    private ChartMetricRemoteRestCaller chartMetricCaller;

    @Inject
    private ChartDataCollectionRemoteRestCaller chartDataCollectionCaller;

    @Inject
    private KafkaTopicSubscriberManager kafkaManager;

    public TradingEvaluationResult predict() {
        LocalDateTime currentTime = LocalDateTime.now().minusMinutes(1).withSecond(0).withNano(0);
        List<ChartMetricRecord> metrics = chartMetricCaller.metric24h();

        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv24h();

        if (metrics != null && chartEntries != null) {

            DataPreparator preparator = new DataPreparator().withChartData(chartEntries).withMetrics(metrics);
            NetInputItem item = preparator.getDataAtTime(currentTime);
            if (item != null) {
                TradingEvaluationResult result = nn.predict(item);
                result.setCurrentTime(currentTime);
                result.setTargetTime(currentTime.plusMinutes(preparator.getOffsetMinutes()));

                return result;
            } else {
                logger.error("could not generate training data");
            }
        }
        return null;
    }

    @Override
    protected void start() {
        timer.start();
        kafkaManager.start();
    }

    @Override
    protected void stop() {
        timer.stop();
        kafkaManager.stop();
    }

    public List<TradingEvaluationResult> getPrediction(LocalDateTime dateTime) {
        return dao.findAfterDate(dateTime);
    }

    public List<TradingEvaluationResult> getAllPredictions() {
        return dao.findAll();
    }

}
