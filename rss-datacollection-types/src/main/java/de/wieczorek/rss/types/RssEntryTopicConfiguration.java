package de.wieczorek.rss.types;

import de.wieczorek.core.kafka.KafkaTopicConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RssEntryTopicConfiguration extends KafkaTopicConfiguration<RssEntry> {

    @Override
    public String getTopic() {
        return "rss-data";
    }

    @Override
    public Class<RssEntry> getType() {
        return RssEntry.class;
    }
}
