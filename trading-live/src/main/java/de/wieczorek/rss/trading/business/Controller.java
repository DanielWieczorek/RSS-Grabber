package de.wieczorek.rss.trading.business;

import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.trading.common.oracle.DefaultOracle;
import de.wieczorek.rss.trading.common.oracle.Oracle;
import de.wieczorek.rss.trading.common.oracle.OracleConfigurationDao;
import de.wieczorek.rss.trading.persistence.LiveAccount;
import de.wieczorek.rss.trading.persistence.LiveAccountDao;
import de.wieczorek.rss.trading.persistence.PerformedTrade;
import de.wieczorek.rss.trading.persistence.PerformedTradeDao;
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
    private Trader trader;

    @Inject
    private RecurrentTaskManager timer;

    private Oracle oracle;

    @Inject
    private PerformedTradeDao tradeDao;

    @Inject
    private LiveAccountDao liveAccountDao;

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


    public List<PerformedTrade> getTrades24h() {
        return tradeDao.find24h();
    }

    public List<LiveAccount> getAccount24h() {
        return liveAccountDao.find24h();
    }
}
