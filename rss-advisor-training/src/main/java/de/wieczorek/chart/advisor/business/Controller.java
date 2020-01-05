package de.wieczorek.chart.advisor.business;

import de.wieczorek.chart.advisor.types.DataGenerator;
import de.wieczorek.chart.advisor.types.TradingNeuralNetworkTrainer;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private TradingNeuralNetworkTrainer nn;

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private DataGenerator generator;

    public void trainNeuralNetwork() {
        nn.train(generator, 1);
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
