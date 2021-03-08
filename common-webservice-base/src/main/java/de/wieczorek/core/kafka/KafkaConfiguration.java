package de.wieczorek.core.kafka;

public class KafkaConfiguration {

    private final String topicName;
    private final Class<?> inputType;

    public KafkaConfiguration(String topicName, Class<?> inputType) {
        this.topicName = topicName;
        this.inputType = inputType;
    }

    public String getTopicName() {
        return topicName;
    }

    public Class<?> getInputType() {
        return inputType;

    }
}
