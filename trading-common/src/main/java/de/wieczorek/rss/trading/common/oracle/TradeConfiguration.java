package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;

public class TradeConfiguration {

    private int threshold;
    private int averageTime;

    private Comparison comparison = Comparison.LOWER;

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
}
