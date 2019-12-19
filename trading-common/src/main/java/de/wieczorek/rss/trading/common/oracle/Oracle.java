package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.StateEdge;

public interface Oracle {
    TradingDecision nextAction(StateEdge snapshot);

    void resetBuy();

    void resetStopLoss();

    void resetSell();
}
