package de.wieczorek.chart.advisor.types.ui;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;

import java.util.List;

public interface CallableResource {

    TradingEvaluationResult predictNow();

    List<TradingEvaluationResult> predict(String offset);


    List<TradingEvaluationResult> getAllSentiments();

}
