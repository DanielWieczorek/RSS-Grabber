package de.wieczorek.rss.trading.config;

import java.util.List;

public class ServiceConfiguration {
    private String apiKey;
    private String authCookie;
    private List<BuyConfiguration> buyConfigurations;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAuthCookie() {
        return authCookie;
    }

    public void setAuthCookie(String authCookie) {
        this.authCookie = authCookie;
    }

    public List<BuyConfiguration> getBuyConfigurations() {
        return buyConfigurations;
    }

    public void setBuyConfigurations(List<BuyConfiguration> buyConfigurations) {
        this.buyConfigurations = buyConfigurations;
    }
}
