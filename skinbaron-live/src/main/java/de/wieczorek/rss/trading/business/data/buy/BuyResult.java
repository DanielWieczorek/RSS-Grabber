package de.wieczorek.rss.trading.business.data.buy;

import java.util.List;

public class BuyResult {
    private List<BuyResultItem> items;
    private String security_token;
    private double total;
    private List<String> generalErrors;

    public List<String> getGeneralErrors() {
        return generalErrors;
    }

    public void setGeneralErrors(List<String> generalErrors) {
        this.generalErrors = generalErrors;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<BuyResultItem> getItems() {
        return items;
    }

    public void setItems(List<BuyResultItem> items) {
        this.items = items;
    }

    public String getSecurity_token() {
        return security_token;
    }

    public void setSecurity_token(String security_token) {
        this.security_token = security_token;
    }
}
