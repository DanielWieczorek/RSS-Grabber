package de.wieczorek.chart.advisor.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.wieczorek.chart.advisor.types.TradingNeuralNetworkTrainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
