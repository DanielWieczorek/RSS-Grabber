package de.wieczorek.rss.trading.business.data.search;

import de.wieczorek.rss.trading.business.data.buy.Offer;

import java.util.List;

public class SearchResult {
    private List<Offer> sales;
    private String message;

    public List<Offer> getSales() {
        return sales;
    }

    public void setSales(List<Offer> sales) {
        this.sales = sales;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
