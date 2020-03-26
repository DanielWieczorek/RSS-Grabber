package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultOracle implements Oracle {

    private static final Logger logger = LoggerFactory.getLogger(DefaultOracle.class);


    private final Predicate<OracleInput> buyDecider;
    private final Predicate<OracleInput> sellDecider;


    public DefaultOracle(OracleConfiguration configuration) {
        buyDecider = buildDecider(configuration.getBuyConfigurations(), configuration.getBuyOperators(), Account::getEur);
        sellDecider = buildDecider(configuration.getSellConfigurations(), configuration.getSellOperators(), Account::getBtc);
    }


    private Predicate<OracleInput> buildDecider(List<TradeConfiguration> buyConfigurations, List<Operator> buyOperators, Function<Account, Double> currencyAmountGetter) {
        Predicate<OracleInput> decider;
        List<TradeDecider> buyDecidersChildren = buyConfigurations
                .stream().map(config -> new TradeDecider(config, currencyAmountGetter)).collect(Collectors.toList());

        if (!buyOperators.isEmpty()) {
            Predicate<OracleInput> rootBuyDecider = buyOperators.get(0).getCombinationFunction()
                    .apply(buyDecidersChildren.get(0), buyDecidersChildren.get(1));
            for (int i = 1; i < buyOperators.size(); i++) {
                rootBuyDecider = buyOperators.get(i).getCombinationFunction().apply(rootBuyDecider, buyDecidersChildren.get(i + 1));
            }
            decider = rootBuyDecider;

        } else {
            if (!buyDecidersChildren.isEmpty()) {
                decider = (x) -> buyDecidersChildren.get(0).test(x);
            } else {
                decider = (x) -> false;
            }
        }
        return decider;
    }

    @Override
    public TradingDecision nextAction(OracleInput input) {
        boolean canSell = input.getStateEdge().getAccount().getBtc() > 0;
        boolean canBuy = input.getStateEdge().getAccount().getEur() > 0;

        double currentPrice;
        currentPrice = input.getStateEdge().getAllStateParts().get(input.getStateEdge().getPartsEndIndex()).getChartEntry().getClose();


        if (canBuy) {
            logger.debug("checking BUY");
            if (buyDecider.test(input)) {
                logger.debug("decision BUY");
                return new TradingDecision(ActionVertexType.BUY, DecisionReason.TRADE);
            } else {
                logger.debug("decision: DO NOTHING");
                return new TradingDecision(ActionVertexType.SELL, DecisionReason.TRADE); // do nothing
            }
        } else {
            logger.debug("checking SELL");
            if (sellDecider.test(input)) {
                logger.debug("decision: SELL");
                return new TradingDecision(ActionVertexType.SELL, DecisionReason.TRADE);
            } else {
                logger.debug("decision: DO NOTHING");
                return new TradingDecision(ActionVertexType.BUY, DecisionReason.TRADE); // do nothing
            }
        }
    }
}
