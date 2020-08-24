package de.wieczorek.rss.trading.business.data.inventory;

public class InventoryQueryData {
    private String apikey;
    private int appid;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }
}
