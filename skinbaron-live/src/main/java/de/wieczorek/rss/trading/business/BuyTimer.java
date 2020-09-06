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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            logger.debug("starting buy");

            List<MarginMetaInfo> margins = new ArrayList<>();
            for (BuyConfiguration buyConfig : config.getBuyConfigurations()) {
                List<Offer> items;
                logger.debug("checking offers for " + buyConfig.getProductName());
                try {
                    double marketPrice = controller.determineSellPrice(buyConfig);
                    double maxBuyPrice = controller.determineBuyPrice(buyConfig);
                    logger.debug("determined max buy price for '" + buyConfig.getProductName() + "'to be: " + maxBuyPrice);
                    items = controller.getOffers(buyConfig.getProductName(), maxBuyPrice);

                    items.forEach(item -> {
                        MarginMetaInfo info = new MarginMetaInfo();
                        info.offer = item;
                        info.marginPercent = (marketPrice - item.getPrice()) / marketPrice * 100;
                        margins.add(info);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                logger.debug("found " + items.size() + " matching offers for " + buyConfig.getProductName());

            }

            logger.debug("found " + margins.size() + " matching offers in total");


            List<Offer> offersSortedByMargin = margins.stream()
                    .sorted(Comparator.comparingDouble(MarginMetaInfo::getMarginPercent).reversed())
                    .map(x -> x.offer)
                    .collect(Collectors.toList());

            if (!offersSortedByMargin.isEmpty()) {
                try {
                    logger.debug("triggering buy");
                    controller.performBuyChunked(offersSortedByMargin, 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            logger.debug("finished buy");

        } catch (Exception e) {
            logger.error("error while triggering buy: ", e);
        }
    }


    private class MarginMetaInfo {
        private Offer offer;
        private double marginPercent;

        public double getMarginPercent() {
            return marginPercent;
        }
    }
}
