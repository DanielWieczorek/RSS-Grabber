package de.wieczorek.chart.core.persistence;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chart_metric")
public class ChartMetricRecord {

    @EmbeddedId
    private ChartMetricId id;

    @Column(name = "value_1_min")
    private double value1min;

    @Column(name = "value_5_min")
    private double value5min;

    @Column(name = "value_15_min")
    private double value15min;

    @Column(name = "value_30_min")
    private double value30min;

    @Column(name = "value_60_min")
    private double value60min;

    public double getValue1min() {
	return value1min;
    }

    public void setValue1min(double value1min) {
	this.value1min = value1min;
    }

    public double getValue5min() {
	return value5min;
    }

    public void setValue5min(double value5min) {
	this.value5min = value5min;
    }

    public double getValue15min() {
	return value15min;
    }

    public void setValue15min(double value15min) {
	this.value15min = value15min;
    }

    public double getValue30min() {
	return value30min;
    }

    public void setValue30min(double value30min) {
	this.value30min = value30min;
    }

    public double getValue60min() {
	return value60min;
    }

    public void setValue60min(double value60min) {
	this.value60min = value60min;
    }

    public ChartMetricId getId() {
	return id;
    }

    public void setId(ChartMetricId id) {
	this.id = id;
    }

}
