package de.wieczorek.rss.trading.common.oracle;

import java.util.List;
import java.util.Objects;

public class OracleConfiguration {

    private List<TradeConfiguration> buyConfigurations;
    private List<Integer> buyRatioPercent;
    private List<TradeConfiguration> sellConfigurations;
    private List<Integer> sellRatioPercent;
    private int buyThresholdAbsolute;
    private int sellThresholdAbsolute;

    public int getBuyThresholdAbsolute() {
        return buyThresholdAbsolute;
    }

    public void setBuyThresholdAbsolute(int buyThresholdAbsolute) {
        this.buyThresholdAbsolute = buyThresholdAbsolute;
    }

    public int getSellThresholdAbsolute() {
        return sellThresholdAbsolute;
    }

    public void setSellThresholdAbsolute(int sellThresholdAbsolute) {
        this.sellThresholdAbsolute = sellThresholdAbsolute;
    }

    public List<TradeConfiguration> getBuyConfigurations() {
        return buyConfigurations;
    }

    public void setBuyConfigurations(List<TradeConfiguration> buyConfigurations) {
        this.buyConfigurations = buyConfigurations;
    }

    public List<TradeConfiguration> getSellConfigurations() {
        return sellConfigurations;
    }

    public void setSellConfigurations(List<TradeConfiguration> sellConfigurations) {
        this.sellConfigurations = sellConfigurations;
    }

    public List<Integer> getBuyRatioPercent() {
        return buyRatioPercent;
    }

    public void setBuyRatioPercent(List<Integer> buyRatioPercent) {
        this.buyRatioPercent = buyRatioPercent;
    }

    public List<Integer> getSellRatioPercent() {
        return sellRatioPercent;
    }

    public void setSellRatioPercent(List<Integer> sellRatioPercent) {
        this.sellRatioPercent = sellRatioPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OracleConfiguration that = (OracleConfiguration) o;
        return Objects.equals(buyConfigurations, that.buyConfigurations) &&
                Objects.equals(buyRatioPercent, that.buyRatioPercent) &&
                Objects.equals(sellConfigurations, that.sellConfigurations) &&
                Objects.equals(sellRatioPercent, that.sellRatioPercent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buyConfigurations, buyRatioPercent, sellConfigurations, sellRatioPercent);
    }
}
