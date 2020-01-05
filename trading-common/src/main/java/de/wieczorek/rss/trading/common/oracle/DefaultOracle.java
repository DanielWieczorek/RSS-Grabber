package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultOracle implements Oracle {

    private static final Logger logger = LoggerFactory.getLogger(DefaultOracle.class);


    private final Predicate<StateEdge> buyDecider;
    private final Predicate<StateEdge> sellDecider;

    private OracleConfiguration configuration;
    private double previousLastMaximumAfterBuy = -1.0;
    private double lastMaximumAfterBuy = -1.0;

    private int lastStopLossTriggerTime = Integer.MAX_VALUE;


    public DefaultOracle(OracleConfiguration configuration) {
        this.configuration = configuration;

        List<TradeDecider> buyDecidersChildren = configuration.getBuyConfigurations()
                .stream().map(config -> new TradeDecider(config, Account::getEur)).collect(Collectors.toList());

        List<Operator> buyOperators = configuration.getBuyOperators();
        if (!buyOperators.isEmpty()) {
            Predicate<StateEdge> rootBuyDecider = buyOperators.get(0).getCombinationFunction()
                    .apply(buyDecidersChildren.get(0), buyDecidersChildren.get(1));
            for (int i = 1; i < buyOperators.size(); i++) {
                rootBuyDecider = buyOperators.get(i).getCombinationFunction().apply(rootBuyDecider, buyDecidersChildren.get(i + 1));
            }
            buyDecider = rootBuyDecider;

        } else {
            if (!buyDecidersChildren.isEmpty()) {
                buyDecider = (x) -> buyDecidersChildren.get(0).test(x);
            } else {
                buyDecider = (x) -> false;
            }
        }


        List<TradeDecider> sellDecidersChildren = configuration.getSellConfigurations()
                .stream().map(config -> new TradeDecider(config, Account::getBtc)).collect(Collectors.toList());

        List<Operator> sellOperators = configuration.getSellOperators();
        if (!sellOperators.isEmpty()) {

            Predicate<StateEdge> rootSellDecider = sellOperators.get(0).getCombinationFunction()
                    .apply(sellDecidersChildren.get(0), sellDecidersChildren.get(1));
            for (int i = 1; i < sellOperators.size(); i++) {
                rootSellDecider = sellOperators.get(i).getCombinationFunction().apply(rootSellDecider, sellDecidersChildren.get(i + 1));
            }
            sellDecider = rootSellDecider;
        } else {
            if (!sellDecidersChildren.isEmpty()) {
                sellDecider = (x) -> sellDecidersChildren.get(0).test(x);
            } else {
                sellDecider = (x) -> false;
            }
        }

    }

    @Override
    public TradingDecision nextAction(StateEdge snapshot) {
        boolean canSell = snapshot.getAccount().getBtc() > 0;
        boolean canBuy = snapshot.getAccount().getEur() > 0;

        double currentPrice;
        currentPrice = snapshot.getAllStateParts().get(snapshot.getPartsEndIndex()).getChartEntry().getClose();


        if (canSell && configuration.getStopLossConfiguration().isPresent()) { // stop loss
            logger.debug("evaluating stop loss");
            if (currentPrice < lastMaximumAfterBuy - configuration.getStopLossConfiguration().get().getStopLossThreshold()) {
                previousLastMaximumAfterBuy = lastMaximumAfterBuy;
                lastMaximumAfterBuy = -1.0;
                return new TradingDecision(ActionVertexType.SELL, DecisionReason.STOP_LOSS);
            } else {
                logger.debug("updating last maximum to " + lastMaximumAfterBuy);
                lastMaximumAfterBuy = Math.max(lastMaximumAfterBuy, currentPrice);
            }
        }

        boolean isStopLossDurationElapsed = true;
        logger.debug("checking if stop loss configuration has elapsed");
        if (configuration.getStopLossConfiguration().isPresent()) {
            isStopLossDurationElapsed = lastStopLossTriggerTime >
                    configuration.getStopLossConfiguration().get().getStopLossCooldown(); // if canBuy then last buy did not go through
            logger.debug(isStopLossDurationElapsed ? "stop loss cooldown elapsed" : "stop loss cooldown is not elapsed");

            if (!isStopLossDurationElapsed) {
                lastStopLossTriggerTime++;
                logger.debug("updating stop loss cooldown time to " + lastStopLossTriggerTime);
            }
        }

        if (canBuy) {
            logger.debug("checking BUY");
            if (buyDecider.test(snapshot) && isStopLossDurationElapsed) {
                lastMaximumAfterBuy = currentPrice;
                lastStopLossTriggerTime = 0;
                logger.debug("decision BUY");

                return new TradingDecision(ActionVertexType.BUY, DecisionReason.TRADE);
            } else {
                logger.debug("decision: DO NOTHING");
                return new TradingDecision(ActionVertexType.SELL, DecisionReason.TRADE); // do nothing
            }
        } else {
            logger.debug("checking SELL");
            if (sellDecider.test(snapshot)) {
                lastMaximumAfterBuy = -1.0;
                logger.debug("decision: SELL");
                return new TradingDecision(ActionVertexType.SELL, DecisionReason.TRADE);
            } else {
                logger.debug("decision: DO NOTHING");
                return new TradingDecision(ActionVertexType.BUY, DecisionReason.TRADE); // do nothing
            }
        }
    }

    public void resetBuy() {
        lastMaximumAfterBuy = -1.0;
        lastStopLossTriggerTime = Integer.MAX_VALUE;
    }

    public void resetStopLoss() {
        lastMaximumAfterBuy = previousLastMaximumAfterBuy;
    }

    @Override
    public void resetSell() {
        lastStopLossTriggerTime = Integer.MAX_VALUE;

    }
}
