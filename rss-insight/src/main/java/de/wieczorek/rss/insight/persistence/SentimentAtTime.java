package de.wieczorek.rss.insight.persistence;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sentimentAtTime")
public class SentimentAtTime {
    @Id
    @Column(name = "time")
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
