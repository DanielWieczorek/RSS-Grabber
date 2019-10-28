package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;

public class OracleConfiguration {

    private int sellThreshold;
    private int buyThreshold;
    private int averageTime;

    private Comparison buyComparison = Comparison.GREATER;
    private Comparison sellComparison = Comparison.LOWER;

    private boolean isStopLossActivated = false;

    private int stopLossThreshold = 0;

    public int getSellThreshold() {
        return sellThreshold;
    }

    public void setSellThreshold(int sellThreshold) {
        this.sellThreshold = sellThreshold;
    }

    public int getBuyThreshold() {
        return buyThreshold;
    }

    public void setBuyThreshold(int buyThreshold) {
        this.buyThreshold = buyThreshold;
    }

    public int getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(int averageTime) {
        this.averageTime = averageTime;
    }

    public Comparison getBuyComparison() {
        return buyComparison;
    }

    public void setBuyComparison(Comparison buyComparison) {
        this.buyComparison = buyComparison;
    }

    public Comparison getSellComparison() {
        return sellComparison;
    }

    public void setSellComparison(Comparison sellComparison) {
        this.sellComparison = sellComparison;
    }

    public boolean isStopLossActivated() {
        return isStopLossActivated;
    }

    public void setStopLossActivated(boolean stopLossActivated) {
        isStopLossActivated = stopLossActivated;
    }

    public int getStopLossThreshold() {
        return stopLossThreshold;
    }

    public void setStopLossThreshold(int stopLossThreshold) {
        this.stopLossThreshold = stopLossThreshold;
    }

}
