package de.wieczorek.rss.advisor.types;

import de.wieczorek.core.kafka.KafkaTopicConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RssTradingEvaluationResultTopicConfiguration extends KafkaTopicConfiguration<TradingEvaluationResult> {

    @Override
    public String getTopic() {
        return "rss-trading-evaluation";
    }

    @Override
    public Class<TradingEvaluationResult> getType() {
        return TradingEvaluationResult.class;
    }
}
