package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TradingTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TradingTimer.class);

    @Inject
    private Controller controller;

    @Override
    public void run() {
        try {
            logger.debug("Triggering trading.");
            controller.triggerTrading();
        } catch (Exception e) {
            logger.error("error while triggering trading: ", e);
        }
    }
}
