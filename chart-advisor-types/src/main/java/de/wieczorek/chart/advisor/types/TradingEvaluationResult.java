package de.wieczorek.chart.advisor.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TradingEvaluationResult")
@IdClass(TradingEvaluationResultId.class)
public class TradingEvaluationResult {

    @Column(name = "predictedDelta")
    private double prediction;

    @Id
    @Column(name = "targetTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime targetTime;

    @Id
    @Column(name = "currentTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime currentTime;

    public double getPrediction() {
	return prediction;
    }

    public void setPrediction(double prediction) {
	this.prediction = prediction;
    }

    public LocalDateTime getTargetTime() {
	return targetTime;
    }

    public void setTargetTime(LocalDateTime targetTime) {
	this.targetTime = targetTime;
    }

    public LocalDateTime getCurrentTime() {
	return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
	this.currentTime = currentTime;
    }

}
