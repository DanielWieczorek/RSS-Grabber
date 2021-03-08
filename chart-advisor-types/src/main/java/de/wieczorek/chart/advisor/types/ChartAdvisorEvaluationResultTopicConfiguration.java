package de.wieczorek.chart.advisor.types;

import de.wieczorek.core.kafka.KafkaTopicConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChartAdvisorEvaluationResultTopicConfiguration extends KafkaTopicConfiguration<TradingEvaluationResult> {

    @Override
    public String getTopic() {
        return "chart-advisor";
    }

    @Override
    public Class<TradingEvaluationResult> getType() {
        return TradingEvaluationResult.class;
    }
}
