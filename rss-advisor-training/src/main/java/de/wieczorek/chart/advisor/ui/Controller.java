package de.wieczorek.chart.advisor.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.advisor.types.TradingNeuralNetworkTrainer;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.insight.types.SentimentAtTime;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private TradingNeuralNetworkTrainer nn;

    @Inject
    private RecurrentTaskManager timer;

    public void trainNeuralNetwork() {
	logger.error("foo"); // TODO
	List<SentimentAtTime> sentiments = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:11020/sentiment-at-time").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<SentimentAtTime>>() {
		});

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});

	if (sentiments != null && chartEntries != null) {
	 //   nn.train(new DataPreparator().withChartData(chartEntries).withSentiments(sentiments).getData(), 1);
	}

    }

    @Override
    protected void start() {
	timer.start();
    }

    @Override
    protected void stop() {
	timer.stop();
    }

}
