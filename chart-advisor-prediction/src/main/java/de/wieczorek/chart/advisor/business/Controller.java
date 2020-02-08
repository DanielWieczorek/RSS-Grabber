package de.wieczorek.chart.advisor.business;

import de.wieczorek.chart.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.chart.advisor.types.DataPreparator;
import de.wieczorek.chart.advisor.types.NetInputItem;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.advisor.types.TradingNeuralNetworkPredictor;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.ChartMetricLocalRestCaller;
import de.wieczorek.core.recalculation.Recalculation;
import de.wieczorek.core.recalculation.RecalculationStatusDao;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Inject
    private ChartMetricLocalRestCaller chartMetricCaller;

    @Inject
    private ChartDataCollectionLocalRestCaller chartDataCollectionCaller;

    public TradingEvaluationResult predict() {
        LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
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
        return dao.findAfterDate(LocalDateTime.now().minusHours(24));
    }

    public List<TradingEvaluationResult> getAllPredictions() {
        return dao.findAll();
    }

    public List<TradingEvaluationResult> get24hAbsolutePrediction() {
        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv24h();

        Map<LocalDateTime, Double> timeToChartEntry = chartEntries
                .stream()
                .collect(Collectors.toMap(entry -> entry.getDate(), entry -> entry.getClose(), (y, x) -> y));

        List<TradingEvaluationResult> data = get24hPrediction();


        List<TradingEvaluationResult> rawPredictions = data.stream().filter(item -> timeToChartEntry.containsKey(item.getTargetTime())).map(item -> {
            TradingEvaluationResult result = new TradingEvaluationResult();
            result.setCurrentTime(item.getCurrentTime());
            result.setTargetTime(item.getTargetTime());
            result.setPrediction(item.getPrediction() + timeToChartEntry.get(item.getTargetTime()));
            return result;
        }).collect(Collectors.toList());

        List<TradingEvaluationResult> finalResult = new ArrayList<>();


        for (int i = 2; i < rawPredictions.size(); i++) {
            TradingEvaluationResult before1 = rawPredictions.get(i - 2);
            TradingEvaluationResult before2 = rawPredictions.get(i - 1);

            TradingEvaluationResult current = rawPredictions.get(i - 1);

            current.setPrediction((before1.getPrediction() + before2.getPrediction() + current.getPrediction()) / 3.0);
            finalResult.add(current);
        }

        return finalResult;

    }
}
