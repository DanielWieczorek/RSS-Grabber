package de.wieczorek.core.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public class KafkaSubscriber<T> implements Runnable {


    private KafkaConsumer<String, String> consumer;
    private Consumer<T> processor;

    private Function<String, T> mapper;

    KafkaSubscriber(KafkaTopicConfiguration<T> config, Consumer<T> processor, String serviceName) {
        consumer = new KafkaConsumer<>(config.getProperties(serviceName));
        consumer.subscribe(Collections.singletonList(config.getTopic()));
        this.processor = processor;
        this.mapper = config.getDeserializer();
    }

    @Override
    public void run() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(60));

        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value()); // TODO
            processor.accept(mapper.apply(record.value()));
        }
    }
}
