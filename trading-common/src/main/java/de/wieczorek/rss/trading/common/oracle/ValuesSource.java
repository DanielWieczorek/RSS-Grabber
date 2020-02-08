package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ValuesSource {
    // RSS_SENTIMENT(0, ValuesSource::processRssSentiment),
    CHART_METRIC__CHART_METRIC(0, buildPair(ValuesSource::processChartMetric, ValuesSource::processChartMetric)),
    CHART_DELTA__CHART_DELTA(1, buildPair(ValuesSource::processChartDelta, ValuesSource::processChartDelta)),
    HIGH_SINCE_BUY__CHART_ABSOLUTE(2, buildPair(ValuesSource::calculateHighSinceBuy, ValuesSource::processChartAbsolute)),
    LAST_BUY__CHART_ABSOLUTE(3, buildPair(ValuesSource::calculateLastBuy, ValuesSource::processChartAbsolute));

    private int index;
    private ValueExtractorPair valueExtractor;

    ValuesSource(int index, ValueExtractorPair valueExtractor) {
        this.index = index;
        this.valueExtractor = valueExtractor;
    }

    private static List<Double> calculateLastBuy(OracleInput oracleInput) {
        return Collections.singletonList(oracleInput.getState().getLastBuyPrice());
    }

    private static ValueExtractorPair buildPair(Function<OracleInput, List<Double>> valueExtractor1, Function<OracleInput, List<Double>> valueExtractor2) {
        ValueExtractorPair result = new ValueExtractorPair();
        result.setValueExtractor1(valueExtractor1);
        result.setValueExtractor2(valueExtractor2);
        return result;
    }

    private static List<Double> calculateHighSinceBuy(OracleInput input) {
        return Collections.singletonList(input.getStateEdge().getAllStateParts().stream()
                .map(StateEdgePart::getChartEntry)
                .filter(Objects::nonNull)
                .filter(entry -> entry.getDate().isAfter(input.getState().getLastBuyTime()))
                .map(ChartEntry::getClose)
                .reduce(input.getState().getLastBuyPrice(), Math::max));


    }

    public static List<Double> processRssSentiment(OracleInput input) {
        return input.getStateEdge().getAllStateParts().stream()
                .map(StateEdgePart::getSentiment)
                .filter(Objects::nonNull)
                .map(de.wieczorek.rss.advisor.types.TradingEvaluationResult::getPredictedDelta)
                .collect(Collectors.toList());
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

    public ValueExtractorPair getValueExtractor() {
        return valueExtractor;
    }

}
