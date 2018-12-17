package de.wieczorek.rss.trading.business.data;

public class Account {
    private double btc;
    private double eur;
    private double eurEquivalent;

    public double getBtc() {
	return btc;
    }

    public void setBtc(double btc) {
	this.btc = btc;
    }

    public double getEur() {
	return eur;
    }

    public void setEur(double eur) {
	this.eur = eur;
    }

    public double getEurEquivalent() {
	return eurEquivalent;
    }

    public void setEurEquivalent(double eurEquivalent) {
	this.eurEquivalent = eurEquivalent;
    }

}