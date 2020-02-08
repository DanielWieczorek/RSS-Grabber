package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.average.AverageType;
import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;

public class TradeConfiguration {

    private int threshold;
    private int averageTime;

    private AverageType averageType = AverageType.EMA;

    private int offset;

    private Comparison comparison = Comparison.LOWER;

    private ValuesSource valuesSource = ValuesSource.CHART_METRIC__CHART_METRIC;


    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(int averageTime) {
        this.averageTime = averageTime;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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
}
