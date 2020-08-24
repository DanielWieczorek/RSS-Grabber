package de.wieczorek.rss.trading.business.data.sell;

public class SellQueryItem {
    private long assetId;
    private double price;

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
