package de.wieczorek.rss.trading.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.business.data.ownoffer.ActiveOfferResultItem;
import de.wieczorek.rss.trading.config.BuyConfiguration;
import de.wieczorek.rss.trading.config.ServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RecurrentTask(interval = 1, unit = TimeUnit.HOURS)
@EntityManagerContext
@ApplicationScoped
public class PriceUpdateTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PriceUpdateTimer.class);

    @Inject
    private Controller controller;

    @Inject
    private ServiceConfiguration config;

    @Override
    public void run() {
        try {
            logger.debug("start updating prices");
            List<ActiveOfferResultItem> activeOffers = controller.getOwnActiveOffers();
            logger.debug("found " + activeOffers.size() + " to check prices for");


            logger.debug("setting prices 0.02 higher");
            activeOffers
                    .forEach(item -> {
                        double targetPrice = item.getPrice() + 0.02;
                        logger.debug("Setting price of " + item.getName() + " from " + item.getPrice() + " to " + targetPrice);
                        try {
                            controller.updatePrice(item, targetPrice);
                            logger.debug("successfully set price to " + targetPrice + " for offer " + item.getMetaOfferId());
                        } catch (Exception e) {
                            logger.error("error while setting price to max: ", e);
                        }
                    });


            Map<Long, Double> prices = config.getBuyConfigurations().stream()
                    .collect(Collectors.toMap(BuyConfiguration::getMetaOfferId, item -> controller.determineSellPrice(item), (a, b) -> a));
            activeOffers = controller.getOwnActiveOffers();
            logger.debug("found " + activeOffers.size() + " to check prices for");
            activeOffers
                    .forEach(item -> {
                        logger.debug("Checking price of " + item.getName());

                        if (!prices.containsKey(item.getMetaOfferId())) {
                            logger.debug("unknown metaOfferId " + item.getMetaOfferId());
                            return;
                        }

                        if (Math.abs(item.getPrice() - prices.get(item.getMetaOfferId())) < 0.01) {
                            logger.debug("current price is " + item.getPrice() + " target price is " + prices.get(item.getMetaOfferId()) + " no update needed");
                            return;
                        }

                        logger.debug("Correcting price of " + item.getName() + " from " + item.getPrice() + " to " + prices
                                .get(item.getMetaOfferId()));
                        try {
                            controller.updatePrice(item, prices.get(item.getMetaOfferId()));
                            logger.debug("successfully corrected price of offer " + item.getMetaOfferId());
                        } catch (Exception e) {
                            logger.error("error while correcting price: ", e);
                        }
                    });
            logger.debug("finished updating prices");
        } catch (Exception e) {
            logger.error("error while correcting prices: ", e);
        }
    }
}
