package de.wieczorek.rss.trading.common.io;

import de.wieczorek.rss.trading.types.Context;

import java.time.LocalDateTime;

public class SimulationContext implements Context {

    private LocalDateTime lastBuyTime;

    @Override
    public LocalDateTime getLastBuyTime() {
        return lastBuyTime;
    }

    public void setLastBuyTime(LocalDateTime newlastBuyTime) {
        lastBuyTime = newlastBuyTime;
    }
}
