package de.wieczorek.rss.trading.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.config.BuyConfiguration;
import de.wieczorek.rss.trading.config.ServiceConfiguration;
import de.wieczorek.rss.trading.db.Price;
import de.wieczorek.rss.trading.db.PriceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 15, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class PriceCollectionTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PriceCollectionTimer.class);

    @Inject
    private Controller controller;

    @Inject
    private ServiceConfiguration config;

    @Inject
    private PriceDao dao;

    @Override
    public void run() {
        try {
            LocalDateTime now = LocalDateTime.now();

            logger.debug("start collecting prices");
            for (BuyConfiguration buyConfig : config.getBuyConfigurations()) {

                Price price = new Price();
                price.setMetaofferid(buyConfig.getMetaOfferId());
                price.setMinimum(controller.getMinMarketPrice(buyConfig.getMetaOfferId()));
                price.setTime(now);
                logger.debug("saving price for " + buyConfig.getProductName());
                dao.persist(price);

            }
            logger.debug("finished updating prices");
        } catch (Exception e) {
            logger.error("error while correcting prices: ", e);
        }
    }
}
