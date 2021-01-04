package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.business.data.AccountInfoService;
import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.oracle.Oracle;
import de.wieczorek.rss.trading.common.oracle.OracleInput;
import de.wieczorek.rss.trading.common.oracle.TraderState;
import de.wieczorek.rss.trading.common.oracle.TradingDecision;
import de.wieczorek.rss.trading.persistence.PerformedTrade;
import de.wieczorek.rss.trading.persistence.PerformedTradeDao;
import de.wieczorek.rss.trading.persistence.TradeStatus;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.utils.OrderValuesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.wieczorek.rss.trading.common.trading.BuySellHelper.BUY_OFFSET_ABSOLUTE;
import static de.wieczorek.rss.trading.common.trading.BuySellHelper.SELL_OFFSET_ABSOLUTE;

@ApplicationScoped
public class Trader {


    private static final Logger logger = LoggerFactory.getLogger(Trader.class);
    private OrderValuesHelper helper;
    @Inject
    private AccountInfoService accountInfoService;
    @Inject
    private DataGeneratorBuilder dataGeneratorBuilder;
    @Inject
    private Exchange exchange;
    @Inject
    private PerformedTradeDao tradeDao;

    private CurrencyPairMetaData metadata;

    @PostConstruct
    private void initialize() {
        metadata = exchange.getExchangeMetaData().getCurrencyPairs().get(CurrencyPair.BTC_EUR);
        helper = new OrderValuesHelper(metadata);
    }

    public void trade(Oracle oracle) {
        DataGenerator generator = dataGeneratorBuilder.produceGenerator();

        List<LimitOrder> openOrders = getOpenOrders();
        cancelOrders(openOrders);

        StateEdge current = generator.BuildLastStateEdge(accountInfoService.getAccount());
        OracleInput input = new OracleInput();
        input.setStateEdge(current);
        input.setState(buildState());
        input.setMinOrder(metadata.getMinimumAmount().doubleValue());
        TradingDecision lastAction = oracle.nextAction(input);


        if (lastAction.getDecision() == ActionVertexType.NOTHING) {
            return;
        }

        if (lastAction.getDecision() == ActionVertexType.BUY) {
            performBuy(current.getAccount(), getCurrentPrice(current) + BUY_OFFSET_ABSOLUTE);
        } else {
            performSell(current.getAccount(), getCurrentPrice(current) - SELL_OFFSET_ABSOLUTE);
        }

    }

    private TraderState buildState() {
        List<PerformedTrade> trades = tradeDao.find24h();
        Optional<PerformedTrade> lastBuy = trades.stream()
                .filter(trade -> trade.getType() == ActionVertexType.BUY)
                .reduce((trade1, trade2) -> trade1.getTime().isAfter(trade2.getTime()) ? trade1 : trade2);

        Optional<PerformedTrade> lastSell = trades.stream()
                .filter(trade -> trade.getType() == ActionVertexType.SELL)
                .reduce((trade1, trade2) -> trade1.getTime().isAfter(trade2.getTime()) ? trade1 : trade2);

        TraderState state = new TraderState();
        if (lastBuy.isPresent()
                && lastBuy.get().getStatus() == TradeStatus.PLACED
                && (lastSell.isEmpty()
                || lastSell.get().getStatus() == TradeStatus.CANCELLED
                || lastSell.get().getTime().isBefore(lastBuy.get().getTime()))) {
            state.setLastBuyPrice(lastBuy.get().getPrice());
            state.setLastBuyTime(lastBuy.get().getTime());
        }

        return state;
    }

    private List<LimitOrder> getOpenOrders() {
        try {
            return exchange.getTradeService().getOpenOrders().getOpenOrders();
        } catch (IOException e) {
            logger.error("error while retrieving open orders: ", e);
        }
        return Collections.emptyList();
    }

    private void cancelOrders(List<LimitOrder> orders) {
        orders.stream().map(Order::getId)
                .forEach(orderId -> {
                    try {
                        exchange.getTradeService().cancelOrder(orderId);
                        PerformedTrade trade = tradeDao.find(orderId);
                        if (trade != null) {
                            trade.setStatus(TradeStatus.CANCELLED);
                            tradeDao.update(trade);
                        }
                    } catch (Exception e) {
                        logger.error("error while cancelling order with id " + orderId, e);
                    }
                });
    }

    private void performSell(Account account, double currentPrice) {
        BigDecimal volume = helper.adjustAmount(BigDecimal.valueOf(account.getBtc()));
        LimitOrder order = new LimitOrder(Order.OrderType.ASK,
                volume,
                CurrencyPair.BTC_EUR, null, null, BigDecimal.valueOf(currentPrice));

        try {
            String id = exchange.getTradeService().placeLimitOrder(order);
            PerformedTrade trade = new PerformedTrade();
            trade.setId(id);
            trade.setStatus(TradeStatus.PLACED);
            trade.setAmount(volume.doubleValue());
            trade.setPair(CurrencyPair.BTC_EUR.toString());
            trade.setPrice(currentPrice);
            trade.setTime(LocalDateTime.now());
            trade.setType(ActionVertexType.SELL);
            tradeDao.addTrade(trade);
        } catch (IOException e) {
            logger.error("error while performing sell ", e);
        }

    }

    private void performBuy(Account account, double currentPrice) {
        BigDecimal volume = helper.adjustAmount(BigDecimal.valueOf(account.getEur() / currentPrice));
        LimitOrder order = new LimitOrder(Order.OrderType.BID,
                volume,
                CurrencyPair.BTC_EUR, null, null, BigDecimal.valueOf(currentPrice));
        try {
            String id = exchange.getTradeService().placeLimitOrder(order);
            PerformedTrade trade = new PerformedTrade();
            trade.setId(id);
            trade.setStatus(TradeStatus.PLACED);
            trade.setAmount(volume.doubleValue());
            trade.setPair(CurrencyPair.BTC_EUR.toString());
            trade.setPrice(currentPrice);
            trade.setTime(LocalDateTime.now());
            trade.setType(ActionVertexType.BUY);
            tradeDao.addTrade(trade);
        } catch (IOException e) {
            logger.error("error while performing buy ", e);
        }
    }

    private double getCurrentPrice(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getClose();
    }
}
