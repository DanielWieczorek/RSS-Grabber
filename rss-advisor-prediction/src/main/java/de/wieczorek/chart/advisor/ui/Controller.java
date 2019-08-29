package de.wieczorek.chart.advisor.ui;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.advisor.types.DataPreparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.advisor.types.TradingNeuralNetworkPredictor;
import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.recalculation.Recalculation;
import de.wieczorek.rss.core.recalculation.RecalculationStatusDao;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

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
    private RecalculationStatusDao recalculationDao;

    public TradingEvaluationResult predict() {
        LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
        SentimentEvaluationResult currentSentiment = ClientBuilder.newClient()
                .register(new ObjectMapperContextResolver()).target("http://localhost:11020/sentiment")
                .request(MediaType.APPLICATION_JSON).get(SentimentEvaluationResult.class);

        List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://localhost:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartEntry>>() {
                });

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

    public void recompute() {

        Recalculation recalculation = new Recalculation();
        recalculation.setLastDate(LocalDateTime.of(1900, 1, 1, 1, 1));
        recalculationDao.deleteAll();
        recalculationDao.create(recalculation);
    }

    @Override
    protected void start() {
        timer.start();
    }

    @Override
    protected void stop() {
        timer.stop();
    }

    public List<TradingEvaluationResult> get24hPrediction() {
        LocalDateTime.now().minusHours(24);
        return dao.findAfterDate(LocalDateTime.now().minusHours(24));
    }

    public List<TradingEvaluationResult> getAllPredictions() {
        return dao.findAll();
    }
}
