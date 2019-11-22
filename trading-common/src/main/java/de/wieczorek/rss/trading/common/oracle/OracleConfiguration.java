package de.wieczorek.rss.trading.common.oracle;

import java.util.Optional;

public class OracleConfiguration {

    private TradeConfiguration buyConfiguration;
    private Optional<TradeConfiguration> sellConfiguration = Optional.empty();

    private Optional<StopLossConfiguration> stopLossConfiguration = Optional.empty();

    public TradeConfiguration getBuyConfiguration() {
        return buyConfiguration;
    }

    public void setBuyConfiguration(TradeConfiguration buyConfiguration) {
        this.buyConfiguration = buyConfiguration;
    }

    public Optional<TradeConfiguration> getSellConfiguration() {
        return sellConfiguration;
    }

    public void setSellConfiguration(Optional<TradeConfiguration> sellConfiguration) {
        this.sellConfiguration = sellConfiguration;
    }

    public Optional<StopLossConfiguration> getStopLossConfiguration() {
        return stopLossConfiguration;
    }

    public void setStopLossConfiguration(Optional<StopLossConfiguration> stopLossConfiguration) {
        this.stopLossConfiguration = stopLossConfiguration;
    }

}
