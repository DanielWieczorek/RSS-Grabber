package de.wieczorek.rss.trading.business.data.ownoffer;

public class ActiveOfferResultItem {

    private int amount;
    private long appId;
    private String dateTradeUnlock;
    private String formattedDateCreated;
    private String formattedDateCanceled;
    private String formattedDateSold;
    private String formattedDateTradeUnlock;
    private String formattedState;
    private String imageUrl;
    private boolean isPrivate;
    private boolean isSoldAndPaid;
    private long metaOfferId;
    private String name;
    private String offerLink;
    private double price;
    private String rarityClassName;
    private boolean stackable;
    private String state;

    public String getFormattedDateCanceled() {
        return formattedDateCanceled;
    }

    public void setFormattedDateCanceled(String formattedDateCanceled) {
        this.formattedDateCanceled = formattedDateCanceled;
    }

    public String getFormattedDateSold() {
        return formattedDateSold;
    }

    public void setFormattedDateSold(String formattedDateSold) {
        this.formattedDateSold = formattedDateSold;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getDateTradeUnlock() {
        return dateTradeUnlock;
    }

    public void setDateTradeUnlock(String dateTradeUnlock) {
        this.dateTradeUnlock = dateTradeUnlock;
    }

    public String getFormattedDateCreated() {
        return formattedDateCreated;
    }

    public void setFormattedDateCreated(String formattedDateCreated) {
        this.formattedDateCreated = formattedDateCreated;
    }

    public String getFormattedDateTradeUnlock() {
        return formattedDateTradeUnlock;
    }

    public void setFormattedDateTradeUnlock(String formattedDateTradeUnlock) {
        this.formattedDateTradeUnlock = formattedDateTradeUnlock;
    }

    public String getFormattedState() {
        return formattedState;
    }

    public void setFormattedState(String formattedState) {
        this.formattedState = formattedState;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean getIsSoldAndPaid() {
        return isSoldAndPaid;
    }

    public void setIsSoldAndPaid(boolean soldAndPaid) {
        isSoldAndPaid = soldAndPaid;
    }

    public long getMetaOfferId() {
        return metaOfferId;
    }

    public void setMetaOfferId(long metaOfferId) {
        this.metaOfferId = metaOfferId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfferLink() {
        return offerLink;
    }

    public void setOfferLink(String offerLink) {
        this.offerLink = offerLink;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRarityClassName() {
        return rarityClassName;
    }

    public void setRarityClassName(String rarityClassName) {
        this.rarityClassName = rarityClassName;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
