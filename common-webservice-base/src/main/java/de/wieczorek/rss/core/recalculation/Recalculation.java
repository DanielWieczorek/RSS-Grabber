package de.wieczorek.rss.core.recalculation;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Entity
@Table(name = "Recalculation")
@SequenceGenerator(name = "seq", initialValue = 1, allocationSize = 1, sequenceName = "recalculation_sequence")
public class Recalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private long id;

    @Column(name = "targetTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastDate;

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public LocalDateTime getLastDate() {
	return lastDate;
    }

    public void setLastDate(LocalDateTime lastDate) {
	this.lastDate = lastDate;
    }

}
