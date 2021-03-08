package de.wieczorek.chart.core.business.kafka;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.core.kafka.KafkaTopicConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChartEntryTopicConfiguration extends KafkaTopicConfiguration<ChartEntry> {

    @Override
    public String getTopic() {
        return "chart-data";
    }

    @Override
    public Class<ChartEntry> getType() {
        return ChartEntry.class;
    }
}
