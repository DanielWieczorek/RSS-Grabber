package de.wieczorek.rss.trading.business.data.updateprice;

public class PriceUpdateQueryData {
    private int amount;
    private long metaOfferId;
    private double originalPrice;
    private double price;
    private String state;
    private String tradeLockedUntil;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getMetaOfferId() {
        return metaOfferId;
    }

    public void setMetaOfferId(long metaOfferId) {
        this.metaOfferId = metaOfferId;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTradeLockedUntil() {
        return tradeLockedUntil;
    }

    public void setTradeLockedUntil(String tradeLockedUntil) {
        this.tradeLockedUntil = tradeLockedUntil;
    }
}
