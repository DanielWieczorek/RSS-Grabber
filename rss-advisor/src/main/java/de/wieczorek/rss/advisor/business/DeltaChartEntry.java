package de.wieczorek.rss.advisor.business;

import java.time.LocalDateTime;

public class DeltaChartEntry {
    private LocalDateTime date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volumeWeightedAverage;
    private double volume;
    private double transactions;

    public LocalDateTime getDate() {
	return date;
    }

    public void setDate(LocalDateTime date) {
	this.date = date;
    }

    public double getOpen() {
	return open;
    }

    public void setOpen(double open) {
	this.open = open;
    }

    public double getHigh() {
	return high;
    }

    public void setHigh(double high) {
	this.high = high;
    }

    public double getLow() {
	return low;
    }

    public void setLow(double low) {
	this.low = low;
    }

    public double getClose() {
	return close;
    }

    public void setClose(double close) {
	this.close = close;
    }

    public double getVolumeWeightedAverage() {
	return volumeWeightedAverage;
    }

    public void setVolumeWeightedAverage(double volumeWeightedAverage) {
	this.volumeWeightedAverage = volumeWeightedAverage;
    }

    public double getVolume() {
	return volume;
    }

    public void setVolume(double volume) {
	this.volume = volume;
    }

    public double getTransactions() {
	return transactions;
    }

    public void setTransactions(double transactions) {
	this.transactions = transactions;
    }

}