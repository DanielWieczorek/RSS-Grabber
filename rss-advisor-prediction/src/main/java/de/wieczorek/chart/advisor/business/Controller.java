package de.wieczorek.chart.advisor.business;

import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.advisor.types.DataPreparator;
import de.wieczorek.chart.advisor.types.TradingNeuralNetworkPredictor;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import de.wieczorek.rss.insight.types.ui.RssInsightLocalRestCaller;
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
    private RssInsightLocalRestCaller rssInsightCaller;

    @Inject
    private ChartDataCollectionLocalRestCaller chartDataCollectionCaller;

    public TradingEvaluationResult predict() {
        LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
        SentimentEvaluationResult currentSentiment = rssInsightCaller.now();

        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv24h();

        if (currentSentiment != null && chartEntries != null) {
            SentimentAtTime sentiment = new SentimentAtTime();
            sentiment.setPositiveProbability(currentSentiment.getSummary().getPositiveProbability());
            sentiment.setNegativeProbability(currentSentiment.getSummary().getNegativeProbability());
            sentiment.setSentimentTime(currentTime);
            DataPreparator preparator = new DataPreparator().withChartData(chartEntries);
            TradingEvaluationResult result = nn.predict(preparator.getDataForSentiment(sentiment));
            result.setCurrentTime(currentTime);
            result.setTargetTime(currentTime.plusMinutes(preparator.getOffsetMinutes()));

            return result;
        }
        return null;

    }

    @Override
    protected void start() {
        timer.start();
    }

    @Override
    protected void stop() {
        timer.stop();
    }

    public List<TradingEvaluationResult> getPrediction(LocalDateTime dateTime) {
        return dao.findAfterDate(dateTime);
    }

    public List<TradingEvaluationResult> getAllPredictions() {
        return dao.findAll();
    }
}
