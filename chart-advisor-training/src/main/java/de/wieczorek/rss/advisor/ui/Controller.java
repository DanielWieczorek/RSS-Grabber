package de.wieczorek.rss.advisor.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DataPreparator;
import de.wieczorek.rss.advisor.types.TradingNeuralNetworkTrainer;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private TradingNeuralNetworkTrainer nn;

    @Inject
    private RecurrentTaskManager timer;

    public void trainNeuralNetwork() {
	logger.error("foo"); // TODO
	List<ChartMetricRecord> metrics = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:13000/metric/all").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartMetricRecord>>() {
		});

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});

	if (metrics != null && chartEntries != null) {
	    nn.train(new DataPreparator().withChartData(chartEntries).withMetrics(metrics).getData(), 1);
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
