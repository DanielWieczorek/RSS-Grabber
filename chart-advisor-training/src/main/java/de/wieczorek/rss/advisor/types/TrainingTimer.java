package de.wieczorek.rss.advisor.types;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 0, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingNeuralNetworkTrainer network;

    public TrainingTimer() {

    }

    @Override
    public void run() {
	try {

		List<ChartMetricRecord> metrics = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
				.target("http://wieczorek.io:13000/metric/all").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<ChartMetricRecord>>() {
				});

		List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
				.target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<ChartEntry>>() {
				});

		if (metrics != null && chartEntries != null) {
			network.train(new DataPreparator().withChartData(chartEntries).withMetrics(metrics).getData(), 5);
		}

	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	    e.printStackTrace();
	}
    }
}
