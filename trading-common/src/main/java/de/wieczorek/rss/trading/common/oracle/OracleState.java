package de.wieczorek.rss.trading.common.oracle;

public class OracleState {

    private double stopLoss = 0.0;

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

}
