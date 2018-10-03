package de.wieczorek.rss.advisor.ui;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.business.DataPreparator;
import de.wieczorek.rss.advisor.business.TradingNeuralNetwork;
import de.wieczorek.rss.advisor.persistence.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    private boolean isStarted = false;
    @Inject
    private TradingNeuralNetwork nn;

    @Inject
    private RecurrentTaskManager timer;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
	start();
    }

    public void trainNeuralNetwork() {
	logger.fatal("foo");
	List<SentimentAtTime> sentiments = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:11020/sentiment-at-time").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<SentimentAtTime>>() {
		});

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});

	if (sentiments != null && chartEntries != null) {

	    nn.train(new DataPreparator().withChartData(chartEntries).withSentiments(sentiments).getData(), 1);
	}

    }

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

    public void start() {
	isStarted = true;
	timer.start();
    }

    public void stop() {
	isStarted = false;
	timer.stop();

    }

    public boolean isStarted() {
	return isStarted;
    }

}
