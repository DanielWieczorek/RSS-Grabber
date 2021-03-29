package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC_SENTIMENT_ABSOLUTE(0, ValuesSource::processChartMetricSentimentAbsolute),
    CHART_ABSOLUTE(1, ValuesSource::processChartAbsolute),
    METRIC_RSS_ABSOLUTE_2H(2, ValuesSource::processChartMetricRssAbsolute2Hour),
    METRIC_MACD_ABSOLUTE_2H(3, ValuesSource::processChartMetricMacdAbsolute2Hour),
    METRIC_AROON_ABSOLUTE_2H(4, ValuesSource::processChartMetricAroonAbsolute2Hour),
    METRIC_STOCHASTIC_D_ABSOLUTE_2H(5, ValuesSource::processChartMetricStochasticDAbsolute2Hour),
    METRIC_RSS_ABSOLUTE_6H(6, ValuesSource::processChartMetricRssAbsolute6Hour),
    METRIC_MACD_ABSOLUTE_6H(7, ValuesSource::processChartMetricMacdAbsolute6Hour),
    METRIC_AROON_ABSOLUTE_6H(8, ValuesSource::processChartMetricAroonAbsolute6Hour),
    METRIC_STOCHASTIC_D_ABSOLUTE_6H(9, ValuesSource::processChartMetricStochasticDAbsolute6Hour),
    METRIC_RSS_ABSOLUTE_15M(10, ValuesSource::processChartMetricRssAbsolute15Minutes),
    METRIC_MACD_ABSOLUTE_15M(11, ValuesSource::processChartMetricMacdAbsolute15Minutes),
    METRIC_AROON_ABSOLUTE_15M(12, ValuesSource::processChartMetricAroonAbsolute15Minutes),
    METRIC_STOCHASTIC_D_ABSOLUTE_15M(13, ValuesSource::processChartMetricStochasticDAbsolute15Minutes);

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

    public static Double processChartMetricRssAbsolute2Hour(StateEdgePart input) {
        return processChartMetricAbsolute2Hour(input, metric -> metric.getId().getIndicator().equals("rsi"));
    }

    public static Double processChartMetricMacdAbsolute2Hour(StateEdgePart input) {
        return processChartMetricAbsolute2Hour(input, metric -> metric.getId().getIndicator().equals("macd"));
    }

    public static Double processChartMetricAroonAbsolute2Hour(StateEdgePart input) {
        return processChartMetricAbsolute2Hour(input, metric -> metric.getId().getIndicator().equals("aroon"));
    }

    public static Double processChartMetricStochasticDAbsolute2Hour(StateEdgePart input) {
        return processChartMetricAbsolute2Hour(input, metric -> metric.getId().getIndicator().equals("stochasticD"));
    }

    public static Double processChartMetricAbsolute2Hour(StateEdgePart input, Predicate<ChartMetricRecord> filter) {
        List<ChartMetricRecord> eval = input.getMetrics();
        if (eval == null) {
            return null;
        }
        return eval.stream().filter(filter)
                .map(ChartMetricRecord::getValue2hour).findFirst().orElse(null);

    }

    public static Double processChartMetricRssAbsolute6Hour(StateEdgePart input) {
        return processChartMetricAbsolute6Hour(input, metric -> metric.getId().getIndicator().equals("rsi"));
    }

    public static Double processChartMetricMacdAbsolute6Hour(StateEdgePart input) {
        return processChartMetricAbsolute6Hour(input, metric -> metric.getId().getIndicator().equals("macd"));
    }

    public static Double processChartMetricAroonAbsolute6Hour(StateEdgePart input) {
        return processChartMetricAbsolute6Hour(input, metric -> metric.getId().getIndicator().equals("aroon"));
    }

    public static Double processChartMetricStochasticDAbsolute6Hour(StateEdgePart input) {
        return processChartMetricAbsolute6Hour(input, metric -> metric.getId().getIndicator().equals("stochasticD"));
    }

    public static Double processChartMetricAbsolute6Hour(StateEdgePart input, Predicate<ChartMetricRecord> filter) {
        List<ChartMetricRecord> eval = input.getMetrics();
        if (eval == null) {
            return null;
        }
        return eval.stream().filter(filter)
                .map(ChartMetricRecord::getValue6hour).findFirst().orElse(null);

    }


    public static Double processChartMetricRssAbsolute15Minutes(StateEdgePart input) {
        return processChartMetricAbsolute15Minutes(input, metric -> metric.getId().getIndicator().equals("rsi"));
    }

    public static Double processChartMetricMacdAbsolute15Minutes(StateEdgePart input) {
        return processChartMetricAbsolute15Minutes(input, metric -> metric.getId().getIndicator().equals("macd"));
    }

    public static Double processChartMetricAroonAbsolute15Minutes(StateEdgePart input) {
        return processChartMetricAbsolute15Minutes(input, metric -> metric.getId().getIndicator().equals("aroon"));
    }

    public static Double processChartMetricStochasticDAbsolute15Minutes(StateEdgePart input) {
        return processChartMetricAbsolute15Minutes(input, metric -> metric.getId().getIndicator().equals("stochasticD"));
    }

    public static Double processChartMetricAbsolute15Minutes(StateEdgePart input, Predicate<ChartMetricRecord> filter) {
        List<ChartMetricRecord> eval = input.getMetrics();
        if (eval == null) {
            return null;
        }
        return eval.stream().filter(filter)
                .map(ChartMetricRecord::getValue15min).findFirst().orElse(null);

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
