package de.wieczorek.rss.insight.ui;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.insight.business.DataPreparator;
import de.wieczorek.rss.insight.business.EvaluationResult;
import de.wieczorek.rss.insight.business.RssSentimentNeuralNetwork;
import de.wieczorek.rss.insight.business.SentimentEvaluationResult;
import de.wieczorek.rss.insight.persistence.ChartEntry;
import de.wieczorek.rss.insight.persistence.SentimentAtTime;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    private boolean isStarted = false;
    @Inject
    private RssSentimentNeuralNetwork nn;

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

	    nn.train(new DataPreparator().withChartData(chartEntries).withSentiments(sentiments).getData());
	}

    }

    public EvaluationResult predict() {
	LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
	SentimentEvaluationResult currentSentiment = ClientBuilder.newClient()
		.register(new ObjectMapperContextResolver()).target("http://localhost:11020/sentiment")
		.request(MediaType.APPLICATION_JSON).get(SentimentEvaluationResult.class);

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});

	if (currentSentiment != null && chartEntries != null) {
	    SentimentAtTime sentiment = new SentimentAtTime();
	    sentiment.setPositiveProbability(currentSentiment.getSummary().getPositiveProbability());
	    sentiment.setNegativeProbability(currentSentiment.getSummary().getNegativeProbability());
	    sentiment.setSentimentTime(currentTime);
	    return nn.predict(new DataPreparator().withChartData(chartEntries).getDataForSentiment(sentiment));
	}
	return null;

    }

    public void start() {
	trainNeuralNetwork();
	isStarted = true;
    }

    public void stop() {
	isStarted = false;
    }

    public boolean isStarted() {
	return isStarted;
    }

}
