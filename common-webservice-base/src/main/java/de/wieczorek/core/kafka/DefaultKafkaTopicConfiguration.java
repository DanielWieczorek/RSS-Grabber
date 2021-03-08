package de.wieczorek.core.kafka;

import java.util.function.Function;

public class DefaultKafkaTopicConfiguration<Void> extends KafkaTopicConfiguration {

    @Override
    public Function<Void, String> getDeserializer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<Void, String> getSerializer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTopic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<Void> getType() {
        throw new UnsupportedOperationException();
    }
}
