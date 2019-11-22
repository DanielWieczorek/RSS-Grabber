package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.trading.common.oracle.DefaultOracle;
import de.wieczorek.rss.trading.common.oracle.OracleConfigurationDao;
import de.wieczorek.rss.trading.common.trading.Trade;
import de.wieczorek.rss.trading.common.trading.TradingSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private OracleConfigurationDao oracleConfigurationDao;

    @Inject
    private TradingSimulator simulator;


    public List<Trade> simulate() {
        return simulator.simulate(new DefaultOracle(oracleConfigurationDao.read()));
    }


}
