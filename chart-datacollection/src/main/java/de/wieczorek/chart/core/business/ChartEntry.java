package de.wieczorek.chart.core.business;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ohlc")
public class ChartEntry {

    @Id
    private Date date;

    private double open;

    private double high;

    private double low;

    private double close;

    private double volumeWeightedAverage;

    private double volume;

    private double transactions;

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
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

    public double getClose() {
	return close;
    }

    public void setClose(double close) {
	this.close = close;
    }

    public double getTransactions() {
	return transactions;
    }

    public void setTransactions(double transactions) {
	this.transactions = transactions;
    }

}
