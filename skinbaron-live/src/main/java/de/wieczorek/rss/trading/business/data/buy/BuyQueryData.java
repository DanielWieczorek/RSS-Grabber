package de.wieczorek.rss.trading.business.data.buy;

import java.util.List;

public class BuyQueryData {
    private String apikey;
    private double total;
    private List<String> saleids;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<String> getSaleids() {
        return saleids;
    }

    public void setSaleids(List<String> saleids) {
        this.saleids = saleids;
    }
}
