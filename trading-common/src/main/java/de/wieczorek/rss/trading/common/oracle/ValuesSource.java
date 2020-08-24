package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.function.Function;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC_ABSOLUTE(0, ValuesSource::processChartMetricAbsolute),
    CHART_ABSOLUTE(1, ValuesSource::processChartAbsolute);


    private int index;
    private Function<StateEdgePart, Double> valueExtractor;

    ValuesSource(int index, Function<StateEdgePart, Double> valueExtractor) {
        this.index = index;
        this.valueExtractor = valueExtractor;
    }

    public static Double processChartMetricAbsolute(StateEdgePart input) {
        TradingEvaluationResult eval = input.getMetricsSentiment();
        if (eval == null) {
            return null;
        }
        return eval.getAbsolutePrediction();
    }

    public static Double processChartAbsolute(StateEdgePart input) {
        ChartEntry eval = input.getChartEntry();
        if (eval == null) {
            return null;
        }
        return eval.getClose();
    }

    public static ValuesSource getValueForIndex(int index) {
        for (ValuesSource src : values()) {
            if (src.index == index) {
                return src;
            }
        }
        throw new RuntimeException("invalid index " + index);
    }

    public int getIndex() {
        return index;
    }

    public Function<StateEdgePart, Double> getValueExtractor() {
        return valueExtractor;
    }

}
