package de.wieczorek.rss.trading.common.oracle;

public class StopLossConfiguration {

    private int stopLossThreshold = 0;

    private int stopLossCooldown = 0;

    public int getStopLossThreshold() {
        return stopLossThreshold;
    }

    public void setStopLossThreshold(int stopLossThreshold) {
        this.stopLossThreshold = stopLossThreshold;
    }

    public int getStopLossCooldown() {
        return stopLossCooldown;
    }

    public void setStopLossCooldown(int stopLossCooldown) {
        this.stopLossCooldown = stopLossCooldown;
    }
}
