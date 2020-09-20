package de.wieczorek.rss.advisor.types.ui;

import de.wieczorek.rss.advisor.types.TradingEvaluationResult;

import java.util.List;

public interface CallableResource {
    TradingEvaluationResult predict();

    List<TradingEvaluationResult> predict24h();

    List<TradingEvaluationResult> getAllSentiments();

}
