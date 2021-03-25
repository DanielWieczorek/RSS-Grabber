package de.wieczorek.rss.trading.business;

import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.trading.business.data.balance.BalanceQueryData;
import de.wieczorek.rss.trading.business.data.balance.BalanceResult;
import de.wieczorek.rss.trading.business.data.buy.BuyQueryData;
import de.wieczorek.rss.trading.business.data.buy.BuyResult;
import de.wieczorek.rss.trading.business.data.buy.Offer;
import de.wieczorek.rss.trading.business.data.inventory.InventoryQueryData;
import de.wieczorek.rss.trading.business.data.inventory.InventoryResult;
import de.wieczorek.rss.trading.business.data.inventory.InventoryResultItem;
import de.wieczorek.rss.trading.business.data.ownoffer.ActiveOfferResultItem;
import de.wieczorek.rss.trading.business.data.ownoffer.ActiveOffersResult;
import de.wieczorek.rss.trading.business.data.price.StackableAvailaiblityTableResult;
import de.wieczorek.rss.trading.business.data.price.StackableAvailaiblityTableResultItem;
import de.wieczorek.rss.trading.business.data.search.SearchQueryData;
import de.wieczorek.rss.trading.business.data.search.SearchResult;
import de.wieczorek.rss.trading.business.data.sell.SellError;
import de.wieczorek.rss.trading.business.data.sell.SellQueryData;
import de.wieczorek.rss.trading.business.data.sell.SellQueryItem;
import de.wieczorek.rss.trading.business.data.sell.SellResult;
import de.wieczorek.rss.trading.business.data.updateprice.PriceUpdateQueryData;
import de.wieczorek.rss.trading.config.BuyConfiguration;
import de.wieczorek.rss.trading.config.ServiceConfiguration;
import de.wieczorek.rss.trading.db.PriceDao;
import de.wieczorek.rss.trading.db.Stock;
import de.wieczorek.rss.trading.db.StockDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);


    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private ServiceConfiguration config;

    @Inject
    private InvocationBuilderCreator invocationBuilderCreator;

    @Inject
    private PriceDao priceDao;

    @Inject
    private StockDao stockDao;

    @Override
    public void start() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        logger.info("started");
        timer.start();
    }

    @Override
    public void stop() {
        logger.info("stopped");
        timer.stop();
    }

    public List<Offer> getOffers(String productName, double maxPrice) {
        final int PAGE_SIZE = 5;
        SearchQueryData data = new SearchQueryData();
        data.setApikey(config.getApiKey());
        data.setAppid(730);
        data.setSearch_item(productName);
        data.setMax(maxPrice);
        data.setMin(0.01);
        data.setItems_per_page(PAGE_SIZE);

        SearchResult result = invocationBuilderCreator.createV1("/Search", config)
                .post(Entity.json(data), SearchResult.class);

        if (result.getMessage() != null) {
            throw new RuntimeException(result.getMessage());
        }

        return result.getSales();
    }

    public List<InventoryResultItem> getInventory() {
        InventoryQueryData data = new InventoryQueryData();
        data.setApikey(config.getApiKey());
        data.setAppid(730);

        InventoryResult result = invocationBuilderCreator.createV2("/api/v2/User/Inventory", config
                , Map.of("appId", 730,
                        "showPricingInformation", true,
                        "offerLimitInfo", true))
                .get(InventoryResult.class);

        if (result.getGeneralErrors() != null) {
            throw new RuntimeException(String.join(", ", result.getGeneralErrors()));
        }

        return result.getItems();
    }


    public void performBuyChunked(List<Offer> items, int chunkSize) {

        List<List<Offer>> partitions = new ArrayList<>();
        for (int i = 0; i < items.size(); i += chunkSize) {
            partitions.add(items.subList(i,
                    Math.min(i + chunkSize, items.size())));
        }

        double balance = getBalance();

        for (int i = 0; i < partitions.size(); i++) {
            logger.debug("triggering buy for chunk " + i);
            try {
                performBuy(partitions.get(i), balance);
                balance = getBalance();
            } catch (Exception e) {
                logger.error("error while triggering buy: ", e);
            }
        }


    }


    public void performBuy(List<Offer> items, double balance) {

        double total = 0;
        List<String> offerIds = new ArrayList<>();

        for (Offer item : items) {
            if (balance < total + item.getPrice()) {
                logger.debug("stopping after " + offerIds.size() + " items");
                break;
            }
            total += item.getPrice();
            offerIds.add(item.getId());
        }
        if (offerIds.isEmpty()) {
            return;
        }

        if (total > balance) {
            throw new RuntimeException("Insufficient Funds");
        }

        BuyQueryData data = new BuyQueryData();
        data.setApikey(config.getApiKey());
        data.setSaleids(offerIds);
        data.setTotal(total);
        BuyResult result = invocationBuilderCreator.createV1("/BuyItems", config)
                .post(Entity.json(data), BuyResult.class);

        if (result.getGeneralErrors() != null) {
            throw new RuntimeException(String.join(", ", result.getGeneralErrors()));
        }

    }

    public void performSellChunked(List<InventoryResultItem> items, int chunkSize) {
        Map<String, Double> prices = config.getBuyConfigurations().stream()
                .collect(Collectors.toMap(BuyConfiguration::getProductName, this::determineSellPrice));
        List<List<InventoryResultItem>> partitions = new ArrayList<>();
        for (int i = 0; i < items.size(); i += chunkSize) {
            partitions.add(items.subList(i,
                    Math.min(i + chunkSize, items.size())));
        }


        for (int i = 0; i < partitions.size(); i++) {
            logger.debug("triggering sell for chunk " + i);
            try {
                performSell(partitions.get(i), prices);
            } catch (Exception e) {
                logger.error("error while triggering sell: ", e);
            }
        }


    }


    public SellResult performSell(List<InventoryResultItem> items, Map<String, Double> prices) {
        List<SellQueryItem> sellItems = items.stream().map(item -> {
            if (!prices.containsKey(item.getLocalizedName())) {
                return null;
            }

            SellQueryItem sellItem = new SellQueryItem();
            sellItem.setAssetId(item.getId());
            sellItem.setPrice(prices.get(item.getLocalizedName()));

            return sellItem;
        }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (sellItems.isEmpty()) {
            return null;
        }

        SellQueryData data = new SellQueryData();
        data.setAppId(730);
        data.setCacheId(null);
        data.setPrivateOffer(false);
        data.setItems(sellItems);

        Response response = invocationBuilderCreator.createV2("/api/v2/User/Inventory/Sell", config)
                .post(Entity.json(data), Response.class);

        SellResult result = response.readEntity(SellResult.class);

        if (result.getErrors() != null) {
            throw new RuntimeException(result.getErrors().stream().map(SellError::getName).collect(Collectors.joining(", ")));
        }

        if (result.getFlashErrorMessage() != null) {
            throw new RuntimeException(result.getFlashErrorMessage());
        }

        return result;
    }


    public double getBalance() {
        logger.debug("Checking balance");
        double total = 0;

        BalanceQueryData data = new BalanceQueryData();
        data.setApikey(config.getApiKey());


        BalanceResult result = invocationBuilderCreator.createV1("/GetBalance", config)
                .post(Entity.json(data), BalanceResult.class);

        logger.debug("balance is " + result.getBalance());
        return result.getBalance();


    }

    public double determineBuyPrice(BuyConfiguration buyConf) {
        final double minMarketPrice = Optional.ofNullable(priceDao.findMinOfLastWeek(buyConf.getMetaOfferId()))
                .orElse(getMinMarketPrice(buyConf.getMetaOfferId()));
        final double fees = Math.max(0.01, Math.ceil(minMarketPrice * 0.15 * 100) / 100.0);

        return Math.round((minMarketPrice - fees - 0.01) * 100.0) / 100.0;
    }


    public double determineSellPrice(BuyConfiguration buyConf) {
        var averageOfLastWeek = priceDao.findAvgOfLastWeek(buyConf.getMetaOfferId());
        return Math.max(Math.round((averageOfLastWeek == null ? 0.0 : averageOfLastWeek) * 100.0) / 100.0,
                Math.round((getMinReasonableMarketPrice(buyConf.getMetaOfferId())) * 100.0) / 100.0);
    }

    public double getMinMarketPrice(long metaOfferId) {
        logger.debug("getting min price for " + metaOfferId);
        StackableAvailaiblityTableResult result = invocationBuilderCreator.createV2Browse("/api/v2/Browsing/StackableAvailabilityTable", config
                , Map.of("metaOfferId", metaOfferId))
                .get(StackableAvailaiblityTableResult.class);

        double price = result.getRows().stream()
                .map(StackableAvailaiblityTableResultItem::getPrice)
                .min(Comparator.comparingDouble(Double::valueOf)).orElse(0.0);
        logger.debug("price is " + price);
        return price;
    }


    private double getMinReasonableMarketPrice(long metaOfferId) {
        logger.debug("getting min price for " + metaOfferId);
        StackableAvailaiblityTableResult result = invocationBuilderCreator.createV2Browse("/api/v2/Browsing/StackableAvailabilityTable", config
                , Map.of("metaOfferId", metaOfferId))
                .get(StackableAvailaiblityTableResult.class);

        double price = result.getRows().stream()
                .filter(item -> item.getAmount() > 1)
                .map(StackableAvailaiblityTableResultItem::getPrice)
                .min(Comparator.comparingDouble(Double::valueOf)).orElse(0.0);
        logger.debug("price is " + price);
        return price;
    }

    public List<ActiveOfferResultItem> getOwnActiveOffers() {
        logger.debug("retrieving own active offers");

        ActiveOffersResult firstResult = invocationBuilderCreator.createV2("/api/v2/Offers", config,
                Map.of("offerFilters", "AVAILABLE",
                        "pagination", URLEncoder.encode("{\"page\":" + 1 + "}", Charset.defaultCharset())))
                .get(ActiveOffersResult.class);
        List<ActiveOfferResultItem> resultItems = new ArrayList<>(firstResult.getOffers());

        for (int i = 2; i <= firstResult.getPaginationResponse().getNumPages(); i++) {
            ActiveOffersResult result = invocationBuilderCreator.createV2("/api/v2/Offers", config,
                    Map.of("offerFilters", "AVAILABLE",
                            "pagination", URLEncoder.encode("{\"page\":" + i + "}", Charset.defaultCharset())))
                    .get(ActiveOffersResult.class);
            resultItems.addAll(result.getOffers());

        }

        return resultItems.stream().filter(item -> item.getState().equals("AVAILABLE")).collect(Collectors.toList());
    }

    public void updatePrice(ActiveOfferResultItem item, double price) {
        PriceUpdateQueryData data = new PriceUpdateQueryData();
        data.setAmount(item.getAmount());
        data.setMetaOfferId(item.getMetaOfferId());
        data.setOriginalPrice(item.getPrice());
        data.setState(item.getState());
        data.setTradeLockedUntil(item.getDateTradeUnlock());
        data.setPrice(price);

        Response response = invocationBuilderCreator.createV2("/api/v2/Offer/EditStack", config)
                .post(Entity.json(data));

        if (response.getStatus() != 200) {
            throw new RuntimeException(response.readEntity(String.class));
        }
    }

    public List<Stock> getStock() {
        return stockDao.findAllCurrent();
    }
}
