package de.wieczorek.core.kafka;

import de.wieczorek.core.config.ServiceName;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KafkaSenderProducer {

    private final Map<Class<? extends KafkaTopicConfiguration>, KafkaSender> senders = new HashMap<>();

    @Inject
    @ServiceName
    private String serviceName;

    @Produces
    @WithTopicConfiguration
    private synchronized KafkaSender buildSender(InjectionPoint ip) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends KafkaTopicConfiguration> configClass = ip.getAnnotated().getAnnotation(WithTopicConfiguration.class).configName();
        KafkaTopicConfiguration<?> config = configClass.getConstructor().newInstance();

        return senders.computeIfAbsent(configClass, (x) -> new KafkaSender<>(config, serviceName));
    }

}
