package de.wieczorek.rss.trading.business.data.buy;

public class Offer {
    private String id;
    private double price;
    private String img;
    private String market_name;
    private String sbinspect;
    private String stickers;
    private int appid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getSbinspect() {
        return sbinspect;
    }

    public void setSbinspect(String sbinspect) {
        this.sbinspect = sbinspect;
    }

    public String getStickers() {
        return stickers;
    }

    public void setStickers(String stickers) {
        this.stickers = stickers;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }
}
