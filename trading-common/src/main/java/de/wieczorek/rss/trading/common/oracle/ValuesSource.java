package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC_SENTIMENT_ABSOLUTE(0, ValuesSource::processChartMetricSentimentAbsolute),
    CHART_ABSOLUTE(1, ValuesSource::processChartAbsolute),
    METRIC_RSS_ABSOLUTE(2, ValuesSource::processChartMetricRssAbsolute),
    METRIC_MACD_ABSOLUTE(3, ValuesSource::processChartMetricMacdAbsolute),
    METRIC_AROON_ABSOLUTE(4, ValuesSource::processChartMetricAroonAbsolute),
    METRIC_STOCHASTIC_D_ABSOLUTE(5, ValuesSource::processChartMetricStochasticDAbsolute),
    LAST_MAX_SINCE_BUY(6, ValuesSource::processLastMaxSinceBuy);

    private int index;
    private Function<StateEdgePart, Double> valueExtractor;

    ValuesSource(int index, Function<StateEdgePart, Double> valueExtractor) {
        this.index = index;
        this.valueExtractor = valueExtractor;
    }

    private static Double processLastMaxSinceBuy(StateEdgePart stateEdgePart) {
        LocalDateTime lastBuyTime = stateEdgePart.getContextProvider().getContext().getLastBuyTime();

        if (lastBuyTime == null || lastBuyTime.isBefore(stateEdgePart.getChartEntry().getDate())) {
            return 0.0;
        }
        return stateEdgePart.getChartEntry().getHigh();
    }

    public static Double processChartMetricSentimentAbsolute(StateEdgePart input) {
        TradingEvaluationResult eval = input.getMetricsSentiment();
        if (eval == null) {
            return null;
        }
        return eval.getAbsolutePrediction();
    }

    public static Double processChartMetricRssAbsolute(StateEdgePart input) {
        return processChartMetricAbsolute(input, metric -> metric.getId().getIndicator().equals("rsi"));
    }

    public static Double processChartMetricMacdAbsolute(StateEdgePart input) {
        return processChartMetricAbsolute(input, metric -> metric.getId().getIndicator().equals("macd"));
    }

    public static Double processChartMetricAroonAbsolute(StateEdgePart input) {
        return processChartMetricAbsolute(input, metric -> metric.getId().getIndicator().equals("aroon"));
    }

    public static Double processChartMetricStochasticDAbsolute(StateEdgePart input) {
        return processChartMetricAbsolute(input, metric -> metric.getId().getIndicator().equals("stochasticD"));
    }

    public static Double processChartMetricAbsolute(StateEdgePart input, Predicate<ChartMetricRecord> filter) {
        List<ChartMetricRecord> eval = input.getMetrics();
        if (eval == null) {
            return null;
        }
        return eval.stream().filter(filter)
                .map(ChartMetricRecord::getValue2hour).findFirst().orElse(null);

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
