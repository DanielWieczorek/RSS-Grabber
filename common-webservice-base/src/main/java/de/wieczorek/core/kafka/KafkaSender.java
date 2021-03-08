package de.wieczorek.core.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.util.Properties;
import java.util.function.Function;

public class KafkaSender<T> {

    private Producer<String, String> producer;
    private Function<T, String> mapper;
    private String topic;
    private Properties properties;


    KafkaSender(KafkaTopicConfiguration<T> config, String serviceName) {
        properties = config.getProperties(serviceName);
        producer = new KafkaProducer<>(config.getProperties(serviceName));
        producer.initTransactions();

        this.mapper = config.getSerializer();
        this.topic = config.getTopic();
    }


    public void send(String id, T obj) {
        try {
            producer.beginTransaction();
            producer.send(new ProducerRecord<>(topic, id, mapper.apply(obj)));
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            producer.close();
            throw new RuntimeException(e);
        } catch (KafkaException e) {
            producer.abortTransaction();
            throw new RuntimeException(e);
        }
    }
}
