package de.wieczorek.rss.advisor.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.advisor.types.NetInputItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DataPreparator;
import de.wieczorek.rss.advisor.types.TradingNeuralNetworkPredictor;
import de.wieczorek.rss.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.recalculation.Recalculation;
import de.wieczorek.rss.core.recalculation.RecalculationStatusDao;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;

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
		List<ChartMetricRecord> metrics = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
				.target("http://wieczorek.io:13000/metric/24h").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<ChartMetricRecord>>() {
				});

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});

	if (metrics != null && chartEntries != null) {

	    DataPreparator preparator = new DataPreparator().withChartData(chartEntries).withMetrics(metrics);
		NetInputItem item = preparator.getDataAtTime(currentTime);
		if(item != null) {
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
		List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
				.target("http://wieczorek.io:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<ChartEntry>>() {
				});
		Map<LocalDateTime,Double> timeToChartEntry  = chartEntries
				.stream()
				.collect(Collectors.toMap(entry -> entry.getDate(),entry -> entry.getClose(),(y,x)-> y));

		List<TradingEvaluationResult>  data = get24hPrediction();


		return data.stream().filter(item -> timeToChartEntry.containsKey(item.getTargetTime())).map(item -> {
			TradingEvaluationResult result = new TradingEvaluationResult();
			result.setCurrentTime(item.getCurrentTime());
			result.setTargetTime(item.getTargetTime());
			result.setPrediction(item.getPrediction()+timeToChartEntry.get(item.getTargetTime()));
			return result;
		}).collect(Collectors.toList());

    }
}
