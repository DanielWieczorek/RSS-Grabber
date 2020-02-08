package de.wieczorek.rss.trading.common.oracle;

import java.time.LocalDateTime;

public class TraderState {

    private double lastBuyPrice = 0;
    private LocalDateTime lastBuyTime = LocalDateTime.MIN;

    public double getLastBuyPrice() {
        return lastBuyPrice;
    }

    public void setLastBuyPrice(double lastBuyPrice) {
        this.lastBuyPrice = lastBuyPrice;
    }

    public LocalDateTime getLastBuyTime() {
        return lastBuyTime;
    }

    public void setLastBuyTime(LocalDateTime lastBuyTime) {
        this.lastBuyTime = lastBuyTime;
    }
}
