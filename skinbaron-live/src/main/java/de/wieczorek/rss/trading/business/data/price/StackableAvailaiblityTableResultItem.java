package de.wieczorek.rss.trading.business.data.price;

public class StackableAvailaiblityTableResultItem {
    private double price;
    private int amount;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
