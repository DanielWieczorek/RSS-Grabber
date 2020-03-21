package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC__CHART_METRIC(0, ValuesSource::processChartMetric),
    CHART_DELTA(1, ValuesSource::processChartDelta),
    CHART_ABSOLUTE(2, ValuesSource::processChartAbsolute);


    private int index;
    private Function<OracleInput, List<Double>> valueExtractor;

    ValuesSource(int index, Function<OracleInput, List<Double>> valueExtractor) {
        this.index = index;
        this.valueExtractor = valueExtractor;
    }

    public static List<Double> processChartMetric(OracleInput input) {
        return input.getStateEdge().getAllStateParts().stream()
                .map(StateEdgePart::getMetricsSentiment)
                .filter(Objects::nonNull)
                .map(TradingEvaluationResult::getPrediction)
                .collect(Collectors.toList());
    }

    public static List<Double> processChartDelta(OracleInput input) {
        return input.getStateEdge().getAllStateParts().stream()
                .map(StateEdgePart::getDeltaChartEntry)
                .filter(Objects::nonNull)
                .map(DeltaChartEntry::getClose)
                .collect(Collectors.toList());
    }

    public static List<Double> processChartAbsolute(OracleInput input) {
        return input.getStateEdge().getAllStateParts().stream()
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

    public Function<OracleInput, List<Double>> getValueExtractor() {
        return valueExtractor;
    }

}
