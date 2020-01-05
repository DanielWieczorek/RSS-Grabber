package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC(0, ValuesSource::processChartMetric),
    CHART(1, ValuesSource::processChart);

    private int index;
    private Function<List<StateEdgePart>, List<Double>> valueExtractor;

    ValuesSource(int index, Function<List<StateEdgePart>, List<Double>> valueExtractor) {
        this.index = index;
        this.valueExtractor = valueExtractor;
    }

    public static List<Double> processRssSentiment(List<StateEdgePart> parts) {
        return parts.stream()
                .map(StateEdgePart::getSentiment)
                .filter(Objects::nonNull)
                .map(de.wieczorek.rss.advisor.types.TradingEvaluationResult::getPredictedDelta)
                .collect(Collectors.toList());
    }

    public static List<Double> processChartMetric(List<StateEdgePart> parts) {
        return parts.stream()
                .map(StateEdgePart::getMetricsSentiment)
                .filter(Objects::nonNull)
                .map(TradingEvaluationResult::getPrediction)
                .collect(Collectors.toList());
    }

    public static List<Double> processChart(List<StateEdgePart> parts) {
        return parts.stream()
                .map(StateEdgePart::getChartEntry)
                .filter(Objects::nonNull)
                .map(ChartEntry::getClose)
                .collect(Collectors.toList());
    }


    public static ValuesSource getValueForIndex(int index) {
        for (ValuesSource src : values()) {
            if (src.index == index) {
                return src;
            }
        }
        throw new RuntimeException("invalid index " + index);
    }

    public Function<List<StateEdgePart>, List<Double>> getValueExtractor() {
        return valueExtractor;
    }

}
