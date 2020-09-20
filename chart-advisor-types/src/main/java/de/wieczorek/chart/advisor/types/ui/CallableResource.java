package de.wieczorek.chart.advisor.types.ui;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;

import java.util.List;

public interface CallableResource {

    TradingEvaluationResult predict();

    List<TradingEvaluationResult> predict24h();


    List<TradingEvaluationResult> getAllSentiments();

}
