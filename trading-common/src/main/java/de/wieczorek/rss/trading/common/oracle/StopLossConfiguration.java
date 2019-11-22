package de.wieczorek.rss.trading.common.oracle;

public class StopLossConfiguration {

    private int stopLossThreshold = 0;

    public int getStopLossThreshold() {
        return stopLossThreshold;
    }

    public void setStopLossThreshold(int stopLossThreshold) {
        this.stopLossThreshold = stopLossThreshold;
    }
}
