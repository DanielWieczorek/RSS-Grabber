package de.wieczorek.rss.insight.types;

import de.wieczorek.core.kafka.KafkaTopicConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RssSentimentTopicConfiguration extends KafkaTopicConfiguration<SentimentEvaluationResult> {

    @Override
    public String getTopic() {
        return "rss-sentiment";
    }

    @Override
    public Class<SentimentEvaluationResult> getType() {
        return SentimentEvaluationResult.class;
    }
}
