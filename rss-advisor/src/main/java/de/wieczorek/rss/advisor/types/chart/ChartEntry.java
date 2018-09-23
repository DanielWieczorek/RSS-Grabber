package de.wieczorek.rss.advisor.types.chart;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Entity
@Table(name = "ohlc")
public class ChartEntry {

    @Id
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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
