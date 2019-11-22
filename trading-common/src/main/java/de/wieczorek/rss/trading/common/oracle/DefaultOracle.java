package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;

import java.util.Optional;

public class DefaultOracle implements Oracle {

    private final TradeDecider buyDecider;
    private Optional<TradeDecider> sellDecider;

    private OracleConfiguration configuration;
    private double lastMaximumAfterBuy = -1.0;


    public DefaultOracle(OracleConfiguration configuration) {
        this.configuration = configuration;
        buyDecider = new TradeDecider(configuration.getBuyConfiguration(), Account::getEur);
        configuration.getSellConfiguration().ifPresentOrElse(
                config -> sellDecider = Optional.of(new TradeDecider(config, Account::getBtc)),
                () -> sellDecider = Optional.empty());


    }

    @Override
    public ActionVertexType nextAction(StateEdge snapshot) {
        boolean canSell = snapshot.getAccount().getBtc() > 0;
        boolean canBuy = snapshot.getAccount().getEur() > 0;

        double currentPrice;
        currentPrice = snapshot.getAllStateParts().get(snapshot.getPartsEndIndex()).getChartEntry().getClose();

        if (canSell && configuration.getStopLossConfiguration().isPresent()) { // stop loss
            if (currentPrice < lastMaximumAfterBuy - configuration.getStopLossConfiguration().get().getStopLossThreshold()) {
                lastMaximumAfterBuy = -1.0;
                return ActionVertexType.SELL;
            } else {
                lastMaximumAfterBuy = Math.max(lastMaximumAfterBuy, currentPrice);
            }
        }

        if (canBuy) {
            if (buyDecider.decide(snapshot)) {
                lastMaximumAfterBuy = currentPrice;
                return ActionVertexType.BUY;
            } else {
                return ActionVertexType.SELL; // do nothing
            }
        } else {
            if (sellDecider.isPresent() && sellDecider.get().decide(snapshot)) {
                lastMaximumAfterBuy = -1.0;
                return ActionVertexType.SELL;
            } else {
                return ActionVertexType.BUY; // do nothing
            }

        }
    }
}
