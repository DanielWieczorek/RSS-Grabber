package de.wieczorek.rss.trading.config;

public class BuyConfiguration {
    private String productName;
    private double maxPrice;
    private long metaOfferId;

    public long getMetaOfferId() {
        return metaOfferId;
    }

    public void setMetaOfferId(long metaOfferId) {
        this.metaOfferId = metaOfferId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
