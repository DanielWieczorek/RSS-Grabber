package de.wieczorek.rss.trading.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.business.data.buy.Offer;
import de.wieczorek.rss.trading.config.BuyConfiguration;
import de.wieczorek.rss.trading.config.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class BuyTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BuyTimer.class);

    @Inject
    private Controller controller;

    @Inject
    private ServiceConfiguration config;

    @Override
    public void run() {
        try {
            List<Offer> allOffers = new ArrayList<>();
            logger.debug("starting timer");

            for (BuyConfiguration buyConfig : config.getBuyConfigurations()) {
                logger.debug("checking offers for " + buyConfig.getProductName());
                List<Offer> items = controller.getOffers(buyConfig.getProductName(), buyConfig.getMaxPrice());
                logger.debug("found " + items.size() + " matching offers for " + buyConfig.getProductName());
                allOffers.addAll(items);
            }
            logger.debug("found " + allOffers.size() + " in total");

            if (!allOffers.isEmpty()) {
                logger.debug("triggering buy");
                controller.performBuyChunked(allOffers, 10);
            }
        } catch (Exception e) {
            logger.error("error while triggering trading: ", e);
        }
    }
}
