package de.wieczorek.rss.trading.business.data.sell;

import java.util.List;

public class SellQueryData {
    private int appId;
    private boolean privateOffer;
    private String cacheId;

    private List<SellQueryItem> items;

    public List<SellQueryItem> getItems() {
        return items;
    }

    public void setItems(List<SellQueryItem> items) {
        this.items = items;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public boolean isPrivateOffer() {
        return privateOffer;
    }

    public void setPrivateOffer(boolean privateOffer) {
        this.privateOffer = privateOffer;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }
}
