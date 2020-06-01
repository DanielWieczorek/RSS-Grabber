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

    @Column(name = "value_2_hour")
    private double value2hour;

    @Column(name = "value_6_hour")
    private double value6hour;

    @Column(name = "value_12_hour")
    private double value12hour;

    @Column(name = "value_24_hour")
    private double value24hour;

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

    public double getValue2hour() {
        return value2hour;
    }

    public void setValue2hour(double value2hour) {
        this.value2hour = value2hour;
    }

    public double getValue6hour() {
        return value6hour;
    }

    public void setValue6hour(double value6hour) {
        this.value6hour = value6hour;
    }

    public double getValue12hour() {
        return value12hour;
    }

    public void setValue12hour(double value12hour) {
        this.value12hour = value12hour;
    }

    public double getValue24hour() {
        return value24hour;
    }

    public void setValue24hour(double value24hour) {
        this.value24hour = value24hour;
    }

    public ChartMetricId getId() {
        return id;
    }

    public void setId(ChartMetricId id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChartMetricRecord other = (ChartMetricRecord) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }

}
