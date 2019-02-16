package de.wieczorek.rss.insight.types;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Entity
@Table(name = "sentimentAtTime")
public class SentimentAtTime {
    @Id
    @Column(name = "time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sentimentTime;

    private double positiveProbability;

    private double negativeProbability;

    public LocalDateTime getSentimentTime() {
	return sentimentTime;
    }

    public void setSentimentTime(LocalDateTime sentimentTime) {
	this.sentimentTime = sentimentTime;
    }

    public double getPositiveProbability() {
	return positiveProbability;
    }

    public void setPositiveProbability(double positiveProbability) {
	this.positiveProbability = positiveProbability;
    }

    public double getNegativeProbability() {
	return negativeProbability;
    }

    public void setNegativeProbability(double negativeProbability) {
	this.negativeProbability = negativeProbability;
    }

}
