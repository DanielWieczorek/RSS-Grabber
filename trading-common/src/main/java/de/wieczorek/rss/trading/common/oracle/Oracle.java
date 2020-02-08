package de.wieczorek.rss.trading.common.oracle;

public interface Oracle {
    TradingDecision nextAction(OracleInput input);
}
