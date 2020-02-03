package de.wieczorek.rss.trading.common.oracle;

import java.util.List;

public class OracleConfiguration {

    private List<TradeConfiguration> buyConfigurations;
    private List<Operator> buyOperators;
    private List<TradeConfiguration> sellConfigurations;
    private List<Operator> sellOperators;


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

    public List<Operator> getBuyOperators() {
        return buyOperators;
    }

    public void setBuyOperators(List<Operator> buyOperators) {
        this.buyOperators = buyOperators;
    }

    public List<Operator> getSellOperators() {
        return sellOperators;
    }

    public void setSellOperators(List<Operator> sellOperators) {
        this.sellOperators = sellOperators;
    }
}
