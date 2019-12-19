package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.trading.business.data.Trader;
import de.wieczorek.rss.trading.common.oracle.DefaultOracle;
import de.wieczorek.rss.trading.common.oracle.Oracle;
import de.wieczorek.rss.trading.common.oracle.OracleConfigurationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private OracleConfigurationDao oracleConfigurationDao;

    @Inject
    private Trader trader;

    @Inject
    private RecurrentTaskManager timer;

    private Oracle oracle;

    @Override
    public void start() {
        oracle = new DefaultOracle(oracleConfigurationDao.read());
        logger.info("started");
        timer.start();
    }

    @Override
    public void stop() {
        oracle = null;
        logger.info("stopped");
        timer.stop();
    }


    public void triggerTrading() {
        if (oracle != null) {
            trader.trade(oracle);
        }

    }


}
