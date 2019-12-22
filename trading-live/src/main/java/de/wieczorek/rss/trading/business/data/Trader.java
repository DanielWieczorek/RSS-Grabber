package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.oracle.DecisionReason;
import de.wieczorek.rss.trading.common.oracle.Oracle;
import de.wieczorek.rss.trading.common.oracle.TradingDecision;
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
import java.util.List;

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

    private TradingDecision lastAction;


    @PostConstruct
    private void initialize() {
        CurrencyPairMetaData metadata = exchange.getExchangeMetaData().getCurrencyPairs().get(CurrencyPair.BTC_EUR);
        helper = new OrderValuesHelper(metadata);
    }

    public void trade(Oracle oracle) {

        DataGenerator generator = dataGeneratorBuilder.produceGenerator();


        List<LimitOrder> openOrders = getOpenOrders();
        cancelOrders(openOrders);
        resetOracle(oracle, openOrders);

        StateEdge current = generator.BuildLastStateEdge(accountInfoService.getAccount());
        lastAction = oracle.nextAction(current);


        if (!isNoOperation(current, lastAction.getDecision())) {
            if (lastAction.getDecision() == ActionVertexType.BUY) {
                performBuy(current.getAccount(), getCurrentPrice(current));
            } else {
                performSell(current.getAccount(), getCurrentPrice(current));
            }
        }
    }

    private void resetOracle(Oracle oracle, List<LimitOrder> openOrders) {
        if (!openOrders.isEmpty()) {
            if (lastAction != null && lastAction.getReason() == DecisionReason.STOP_LOSS) {
                oracle.resetStopLoss();
            }

            if (lastAction != null
                    && lastAction.getReason() == DecisionReason.TRADE
                    && lastAction.getDecision() == ActionVertexType.BUY) {
                oracle.resetBuy();
            }

            if (lastAction != null
                    && lastAction.getReason() == DecisionReason.TRADE
                    && lastAction.getDecision() == ActionVertexType.SELL) {
                oracle.resetSell();
            }
        }
    }

    private List<LimitOrder> getOpenOrders() {
        try {
            return exchange.getTradeService().getOpenOrders().getOpenOrders();
        } catch (IOException e) {
            logger.error("error while retrieving open orders: ", e);
        }
        return null;
    }

    private void cancelOrders(List<LimitOrder> orders) {

        orders
                .stream()
                .map(Order::getId).forEach(orderId -> {
            try {
                exchange.getTradeService().cancelOrder(orderId);
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
            exchange.getTradeService().placeLimitOrder(order);
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
            exchange.getTradeService().placeLimitOrder(order);
        } catch (IOException e) {
            logger.error("error while performing buy ", e);
        }
    }

    private boolean isNoOperation(StateEdge current, ActionVertexType nextAction) {
        if (nextAction == ActionVertexType.BUY &&
                helper.amountUnderMinimum(BigDecimal.valueOf(current.getAccount().getEur()))) {
            return true;
        }

        if (nextAction == ActionVertexType.SELL &&
                helper.amountUnderMinimum(BigDecimal.valueOf(current.getAccount().getBtc()))) {
            return true;
        }

        return false;
    }

    private double getCurrentPrice(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getClose();
    }
}
