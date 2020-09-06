package de.wieczorek.rss.trading.business.data.search;

public class SearchQueryData {
    private String apikey;
    private int appid;
    private String search_item;

    private String after_saleid;
    private double max;
    private double min;
    private int items_per_page;

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public int getItems_per_page() {
        return items_per_page;
    }

    public void setItems_per_page(int items_per_page) {
        this.items_per_page = items_per_page;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getAfter_saleid() {
        return after_saleid;
    }

    public void setAfter_saleid(String after_saleid) {
        this.after_saleid = after_saleid;
    }

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

    public String getSearch_item() {
        return search_item;
    }

    public void setSearch_item(String search_item) {
        this.search_item = search_item;
    }
}
