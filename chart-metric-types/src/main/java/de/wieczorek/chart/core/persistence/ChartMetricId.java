package de.wieczorek.chart.core.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Embeddable
public class ChartMetricId implements Serializable {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    private String indicator;

    public LocalDateTime getDate() {
	return date;
    }

    public void setDate(LocalDateTime date) {
	this.date = date;
    }

    public String getIndicator() {
	return indicator;
    }

    public void setIndicator(String indicator) {
	this.indicator = indicator;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((date == null) ? 0 : date.hashCode());
	result = prime * result + ((indicator == null) ? 0 : indicator.hashCode());
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
	ChartMetricId other = (ChartMetricId) obj;
	if (date == null) {
	    if (other.date != null) {
		return false;
	    }
	} else if (!date.equals(other.date)) {
	    return false;
	}
	if (indicator == null) {
	    if (other.indicator != null) {
		return false;
	    }
	} else if (!indicator.equals(other.indicator)) {
	    return false;
	}
	return true;
    }

}
