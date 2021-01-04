package de.wieczorek.rss.trading.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.business.data.inventory.InventoryResultItem;
import de.wieczorek.rss.trading.config.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 15, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class SellTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SellTimer.class);

    @Inject
    private Controller controller;

    @Inject
    private ServiceConfiguration config;

    @Override
    public void run() {
        try {
            logger.debug("starting timer");
            List<InventoryResultItem> items = controller.getInventory();
            logger.debug("triggering sell of " + items + " items");
            controller.performSellChunked(items, 10);
            logger.debug("sell was successful");
        } catch (Exception e) {
            logger.error("error while triggering trading: ", e);
        }
    }
}
