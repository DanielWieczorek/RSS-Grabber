package de.wieczorek.rss.advisor.business;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.insight.types.SentimentAtTime;

@RecurrentTask(interval = 30, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LogManager.getLogger(TrainingTimer.class.getName());

    @Inject
    private TradingNeuralNetwork network;

    public TrainingTimer() {

    }

    @Override
    public void run() {
	try {

	    List<SentimentAtTime> sentiments = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		    .target("http://localhost:11020/sentiment-at-time").request(MediaType.APPLICATION_JSON)
		    .get(new GenericType<List<SentimentAtTime>>() {
		    });

	    List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		    .target("http://localhost:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		    .get(new GenericType<List<ChartEntry>>() {
		    });

	    if (sentiments != null && chartEntries != null) {
		network.train(new DataPreparator().withChartData(chartEntries).withSentiments(sentiments).getData(), 5);
	    }

	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	}
    }
}
