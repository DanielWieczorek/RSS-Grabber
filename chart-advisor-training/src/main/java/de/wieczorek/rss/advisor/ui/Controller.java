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

    @Override
    protected void start() {
	timer.start();
    }

    @Override
    protected void stop() {
	timer.stop();
    }

}
