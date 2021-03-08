package de.wieczorek.chart.core.business.kafka;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.core.kafka.KafkaTopicConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChartMetricTopicConfiguration extends KafkaTopicConfiguration<ChartMetricRecord> {

    @Override
    public String getTopic() {
        return "chart-metric";
    }

    @Override
    public Class<ChartMetricRecord> getType() {
        return ChartMetricRecord.class;
    }

}
