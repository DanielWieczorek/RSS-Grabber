package de.wieczorek.rss.core.recalculation;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import de.wieczorek.rss.core.db.LocalDateTimeConverter;

@Entity
@Table(name = "Recalculation")
public class Recalculation {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "targetTime")
    @Convert(converter = LocalDateTimeConverter.class)
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
