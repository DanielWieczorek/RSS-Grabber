package de.wieczorek.rss.advisor.types.ui;

import de.wieczorek.rss.advisor.types.TradingEvaluationResult;

import java.util.List;

public interface CallableResource {
    TradingEvaluationResult predictNow();

    List<TradingEvaluationResult> predict(String offset);

    List<TradingEvaluationResult> getAllSentiments();

}
