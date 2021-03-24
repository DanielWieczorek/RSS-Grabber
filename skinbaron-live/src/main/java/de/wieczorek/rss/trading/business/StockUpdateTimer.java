package de.wieczorek.rss.trading.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.business.data.inventory.InventoryResultItem;
import de.wieczorek.rss.trading.business.data.ownoffer.ActiveOfferResultItem;
import de.wieczorek.rss.trading.config.BuyConfiguration;
import de.wieczorek.rss.trading.config.ServiceConfiguration;
import de.wieczorek.rss.trading.db.Stock;
import de.wieczorek.rss.trading.db.StockDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RecurrentTask(interval = 15, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class StockUpdateTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(StockUpdateTimer.class);

    @Inject
    private Controller controller;

    @Inject
    private StockDao dao;
    @Inject
    private ServiceConfiguration config;

    @Override
    public void run() {
        try {
            logger.debug("starting timer");
            Map<String, Long> metaOfferIdMapping = config.getBuyConfigurations().stream()
                    .collect(Collectors.toMap(BuyConfiguration::getProductName, BuyConfiguration::getMetaOfferId));
            List<Stock> inventory = convertInventoryItemsToStock(controller.getInventory(), metaOfferIdMapping);
            List<Stock> activeOffers = convertOffersToStock(controller.getOwnActiveOffers());

            LocalDateTime now = LocalDateTime.now();
            List<Stock> result = Stream.concat(inventory.stream(), activeOffers.stream())
                    .collect(Collectors.groupingBy(Stock::getMetaofferid)).values().stream()
                    .map(this::merge)
                    .collect(Collectors.toList());

            result.forEach(item -> item.setTime(now));
            result.forEach(dao::persist);

            logger.debug("updated stock");
        } catch (Exception e) {
            logger.error("error while updating stock: ", e);
        }
    }

    private Stock merge(List<Stock> stocks) {
        return stocks.stream().reduce((a, b) -> {
            Stock result = new Stock();
            result.setMetaofferid(a.getMetaofferid());
            result.setName(a.getName());
            result.setAmount(a.getAmount() + b.getAmount());
            return result;
        }).orElse(null);
    }

    private List<Stock> convertInventoryItemsToStock(List<InventoryResultItem> data, Map<String, Long> metaOfferIdMapping) {
        List<Stock> result = new ArrayList<>();
        for (InventoryResultItem item : data) {
            Stock stock = new Stock();
            stock.setMetaofferid(metaOfferIdMapping.get(item.getLocalizedName()));
            stock.setName(item.getLocalizedName());
            stock.setAmount(1);
            result.add(stock);
        }

        return result;
    }

    private List<Stock> convertOffersToStock(List<ActiveOfferResultItem> data) {
        List<Stock> result = new ArrayList<>();
        for (ActiveOfferResultItem item : data) {
            Stock stock = new Stock();
            stock.setMetaofferid(item.getMetaOfferId());
            stock.setName(item.getName());
            stock.setAmount(item.getAmount());
            result.add(stock);
        }

        return result;
    }
}
