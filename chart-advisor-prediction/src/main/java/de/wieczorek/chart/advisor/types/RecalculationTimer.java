package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.ChartMetricRemoteRestCaller;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.recalculation.business.AbstractRecalculationTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class RecalculationTimer extends AbstractRecalculationTimer {
    private static final Logger logger = LoggerFactory.getLogger(RecalculationTimer.class);

    private static final int NUMBER_OF_ENTRIES = 300;

    @Inject
    private TradingNeuralNetworkPredictor nn;
    @Inject
    private TradingEvaluationResultDao tradingDao;

    @Inject
    private ChartMetricRemoteRestCaller chartMetricCaller;

    @Inject
    private ChartDataCollectionLocalRestCaller chartDataCollectionCaller;

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {

        LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
        List<ChartMetricRecord> metrics = chartMetricCaller.metricAll();

        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv();

        logger.debug(LocalDateTime.now() + "calculating for " + chartEntries.size() + "entries");

        DataPreparator preparator = new DataPreparator().withChartData(chartEntries).withMetrics(metrics);
        int startIndex = 0;
        for (int i = 0; i < metrics.size(); i++) {
            if (metrics.get(i).getId().getDate().isEqual(startDate)) {
                startIndex = i + 1;
                break;
            }
        }
        int i = 0;
        for (i = 0; i < NUMBER_OF_ENTRIES && i + startIndex < metrics.size(); i++) {
            ChartMetricRecord sentiment = metrics.get(i + startIndex);
            NetInputItem networkInput = preparator.getDataAtTime(sentiment.getId().getDate());
            if (networkInput != null) {
                TradingEvaluationResult result = nn.predict(networkInput);
                result.setCurrentTime(sentiment.getId().getDate());
                result.setTargetTime(sentiment.getId().getDate().plusMinutes(preparator.getOffsetMinutes()));

                tradingDao.upsert(result);
                logger.debug(LocalDateTime.now() + "calculating for date " + sentiment.getId().getDate());
            }
        }

        if (startIndex + NUMBER_OF_ENTRIES < metrics.size()) {
            return metrics.get(i + startIndex).getId().getDate();
        } else {
            return null;
        }

    }

}
