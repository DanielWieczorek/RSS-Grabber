package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.recalculation.business.AbstractRecalculationTimer;
import de.wieczorek.rss.advisor.types.NetInputItem;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.ui.RssInsightLocalRestCaller;
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
    private RssInsightLocalRestCaller rssInsightCaller;

    @Inject
    private ChartDataCollectionLocalRestCaller chartDataCollectionCaller;

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {

        List<SentimentAtTime> sentiments = rssInsightCaller.all();

        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv();

        logger.debug("calculating for " + chartEntries.size() + "entries");

        DataPreparator preparator = new DataPreparator().withChartData(chartEntries);
        int startIndex = 0;
        for (int i = 0; i < sentiments.size(); i++) {
            if (sentiments.get(i).getSentimentTime().isEqual(startDate)) {
                startIndex = i + 1;
                break;
            }
        }
        int i;
        for (i = 0; i < NUMBER_OF_ENTRIES && i + startIndex < sentiments.size(); i++) {
            SentimentAtTime sentiment = sentiments.get(i + startIndex);
            NetInputItem networkInput = preparator.getDataForSentiment(sentiment);
            if (networkInput != null) {
                TradingEvaluationResult result = nn.predict(networkInput);
                result.setCurrentTime(sentiment.getSentimentTime());
                result.setTargetTime(sentiment.getSentimentTime().plusMinutes(preparator.getOffsetMinutes()));

                tradingDao.upsert(result);
                logger.debug("calculating for date " + sentiment.getSentimentTime());
            }
        }

        if (startIndex + NUMBER_OF_ENTRIES < sentiments.size()) {
            return sentiments.get(i + startIndex).getSentimentTime();
        } else {
            return null;
        }

    }

}
