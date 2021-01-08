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
        buyDecider = buildDecider(configuration.getBuyConfigurations(), configuration.getBuyRatioPercent(), configuration.getBuyThresholdAbsolute(), Account::getEur);
        sellDecider = buildDecider(configuration.getSellConfigurations(), configuration.getSellRatioPercent(), configuration.getSellThresholdAbsolute(), Account::getBtc);
    }


    private Predicate<OracleInput> buildDecider(List<TradeConfiguration> buyConfigurations, List<Integer> buyOperators, int buyThresholdAbsolute, Function<Account, Double> currencyAmountGetter) {
        List<TradeDecider> decidersChildren = buyConfigurations
                .stream().map(config -> new TradeDecider(config, currencyAmountGetter)).collect(Collectors.toList());

        return (input) -> {

            int sumPositive = 0;
            for (int i = 0; i < buyOperators.size(); i++) {
                sumPositive += decidersChildren.get(i).test(input) ? buyOperators.get(i) : 0;
                if (sumPositive >= buyThresholdAbsolute) {
                    return true;
                }
            }
            return false;
        };

    }

    @Override
    public TradingDecision nextAction(OracleInput input) {
        logger.debug("checking BUY");
        if (buyDecider.test(input)) {
            logger.debug("decision BUY");
            return new TradingDecision(ActionVertexType.BUY, DecisionReason.TRADE);
        } else {
            logger.debug("checking SELL");
            if (sellDecider.test(input)) {
                logger.debug("decision: SELL");
                return new TradingDecision(ActionVertexType.SELL, DecisionReason.TRADE);
            } else {
                logger.debug("decision: DO NOTHING");
                return new TradingDecision(ActionVertexType.NOTHING, DecisionReason.TRADE); // do nothing
            }
        }
    }
}
