package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.average.AverageType;
import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TradeConfiguration {

    private List<ValuePoint> comparisonPoints = new ArrayList<>();

    private List<Comparison> comparisons = new ArrayList<>();

    private List<Integer> margins = new ArrayList<>();
    private List<Integer> ranges = new ArrayList<>();
    private AverageType averageType = AverageType.EMA;
    private ValuesSource valuesSource = ValuesSource.CHART_METRIC__CHART_METRIC;

    public List<Integer> getRanges() {
        return ranges;
    }

    public void setRanges(List<Integer> ranges) {
        this.ranges = ranges;
    }

    public List<ValuePoint> getComparisonPoints() {
        return comparisonPoints;
    }

    public void setComparisonPoints(List<ValuePoint> comparisonPoints) {
        this.comparisonPoints = comparisonPoints;
    }

    public List<Comparison> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<Comparison> comparisons) {
        this.comparisons = comparisons;
    }

    public AverageType getAverageType() {
        return averageType;
    }

    public void setAverageType(AverageType averageType) {
        this.averageType = averageType;
    }


    public ValuesSource getValuesSource() {
        return valuesSource;
    }

    public void setValuesSource(ValuesSource valuesSource) {
        this.valuesSource = valuesSource;
    }

    public List<Integer> getMargins() {
        return margins;
    }

    public void setMargins(List<Integer> margins) {
        this.margins = margins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeConfiguration that = (TradeConfiguration) o;
        return Objects.equals(comparisonPoints, that.comparisonPoints) &&
                Objects.equals(comparisons, that.comparisons) &&
                Objects.equals(margins, that.margins) &&
                averageType == that.averageType &&
                valuesSource == that.valuesSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(comparisonPoints, comparisons, margins, averageType, valuesSource);
    }
}
