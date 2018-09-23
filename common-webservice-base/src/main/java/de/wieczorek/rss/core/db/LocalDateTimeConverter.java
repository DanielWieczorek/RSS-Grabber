package de.wieczorek.rss.core.db;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm")
	    .toFormatter();

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
	return (localDateTime == null ? null : localDateTime.format(formatter));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String sqlDate) {
	return (sqlDate == null ? null : LocalDateTime.parse(sqlDate, formatter));
    }

}