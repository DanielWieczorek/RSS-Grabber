package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.function.Function;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC_SENTIMENT_ABSOLUTE(0, ValuesSource::processChartMetricSentimentAbsolute),
    CHART_ABSOLUTE(1, ValuesSource::processChartAbsolute),
    METRIC_RSS_ABSOLUTE_2H(2, (x) -> processChartMetric(x, StateEdgePart.Metric.RSI, ChartMetricRecord::getValue2hour)),
    METRIC_MACD_ABSOLUTE_2H(3, (x) -> processChartMetric(x, StateEdgePart.Metric.MACD, ChartMetricRecord::getValue2hour)),
    METRIC_AROON_ABSOLUTE_2H(4, (x) -> processChartMetric(x, StateEdgePart.Metric.AROON, ChartMetricRecord::getValue2hour)),
    METRIC_STOCHASTIC_D_ABSOLUTE_2H(5, (x) -> processChartMetric(x, StateEdgePart.Metric.STOCHASTIC_D, ChartMetricRecord::getValue2hour)),
    METRIC_RSS_ABSOLUTE_6H(6, (x) -> processChartMetric(x, StateEdgePart.Metric.RSI, ChartMetricRecord::getValue6hour)),
    METRIC_MACD_ABSOLUTE_6H(7, (x) -> processChartMetric(x, StateEdgePart.Metric.MACD, ChartMetricRecord::getValue6hour)),
    METRIC_AROON_ABSOLUTE_6H(8, (x) -> processChartMetric(x, StateEdgePart.Metric.AROON, ChartMetricRecord::getValue6hour)),
    METRIC_STOCHASTIC_D_ABSOLUTE_6H(9, (x) -> processChartMetric(x, StateEdgePart.Metric.STOCHASTIC_D, ChartMetricRecord::getValue6hour)),
    METRIC_RSS_ABSOLUTE_15M(10, (x) -> processChartMetric(x, StateEdgePart.Metric.RSI, ChartMetricRecord::getValue15min)),
    METRIC_MACD_ABSOLUTE_15M(11, (x) -> processChartMetric(x, StateEdgePart.Metric.MACD, ChartMetricRecord::getValue15min)),
    METRIC_AROON_ABSOLUTE_15M(12, (x) -> processChartMetric(x, StateEdgePart.Metric.AROON, ChartMetricRecord::getValue15min)),
    METRIC_STOCHASTIC_D_ABSOLUTE_15M(13, (x) -> processChartMetric(x, StateEdgePart.Metric.STOCHASTIC_D, ChartMetricRecord::getValue15min));

    private int index;
    private Function<StateEdgePart, Double> valueExtractor;

    ValuesSource(int index, Function<StateEdgePart, Double> valueExtractor) {
        this.index = index;
        this.valueExtractor = valueExtractor;
    }

    public static Double processChartMetricSentimentAbsolute(StateEdgePart input) {
        TradingEvaluationResult eval = input.getMetricsSentiment();
        if (eval == null) {
            return null;
        }
        return eval.getAbsolutePrediction();
    }

    public static Double processChartMetric(StateEdgePart input, StateEdgePart.Metric metric, Function<ChartMetricRecord, Double> valueExtractor) {
        ChartMetricRecord eval = input.getMetrics().get(metric);
        if (eval == null) {
            return null;
        }
        return valueExtractor.apply(eval);
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
