package de.wieczorek.core.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;

public abstract class KafkaTopicConfiguration<T> {

    private String uuid = UUID.randomUUID().toString();
    private ObjectMapper mapper = new ObjectMapper();

    public Function<T, String> getSerializer() {
        return (foo) -> {
            try {
                return mapper.writeValueAsString(foo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public Function<String, T> getDeserializer() {
        return (str) -> {
            try {
                return mapper.readValue(str, getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

    }

    public Properties getProperties(String serviceName) {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9093");
        props.setProperty("group.id", serviceName);
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("transactional.id", serviceName + "." + getTopic() + "." + uuid);
        props.setProperty("acks", "all");
        props.setProperty("retries", "1");
        props.setProperty("linger.ms", "1");
        return props;
    }

    public abstract String getTopic();

    public abstract Class<T> getType();

}
