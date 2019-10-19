package de.wieczorek.rss.trading.ui;

import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.trading.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private TradingSimulator simulator;


    public List<Trade> simulate() {
        return simulator.simulate(new DefaultOracle(-96,15,18,Comparison.GREATER,Comparison.LOWER,105));
    }



}
