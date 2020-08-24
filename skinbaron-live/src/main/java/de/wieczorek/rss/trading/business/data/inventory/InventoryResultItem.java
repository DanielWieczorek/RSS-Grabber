package de.wieczorek.rss.trading.business.data.inventory;

public class InventoryResultItem {
    private long id;
    private String marketHashName;
    private int tradeLockHoursLeft;
    private boolean isStackable;
    private String imageUrl;
    private String localizedName;
    private String localizedRarityName;
    private double maximumPrice;
    private double minimumPrice;
    private String rarityClassName;
    private double recommendedPrice;
    private double steamMarketPrice;
    private String tradeLockedUntil;
    private long variantId;

    public boolean getIsStackable() {
        return isStackable;
    }

    public void setIsStackable(boolean stackable) {
        isStackable = stackable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getLocalizedRarityName() {
        return localizedRarityName;
    }

    public void setLocalizedRarityName(String localizedRarityName) {
        this.localizedRarityName = localizedRarityName;
    }

    public double getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(double maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getRarityClassName() {
        return rarityClassName;
    }

    public void setRarityClassName(String rarityClassName) {
        this.rarityClassName = rarityClassName;
    }

    public double getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(double recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public double getSteamMarketPrice() {
        return steamMarketPrice;
    }

    public void setSteamMarketPrice(double steamMarketPrice) {
        this.steamMarketPrice = steamMarketPrice;
    }

    public String getTradeLockedUntil() {
        return tradeLockedUntil;
    }

    public void setTradeLockedUntil(String tradeLockedUntil) {
        this.tradeLockedUntil = tradeLockedUntil;
    }

    public long getVariantId() {
        return variantId;
    }

    public void setVariantId(long variantId) {
        this.variantId = variantId;
    }

    public String getMarketHashName() {
        return marketHashName;
    }

    public void setMarketHashName(String marketHashName) {
        this.marketHashName = marketHashName;
    }

    public int getTradeLockHoursLeft() {
        return tradeLockHoursLeft;
    }

    public void setTradeLockHoursLeft(int tradeLockHoursLeft) {
        this.tradeLockHoursLeft = tradeLockHoursLeft;
    }
}
