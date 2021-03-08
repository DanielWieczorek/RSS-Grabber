package de.wieczorek.core.kafka;

import de.wieczorek.core.config.ServiceName;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;

@ApplicationScoped
public class KafkaSenderProducer {

    @Inject
    @ServiceName
    private String serviceName;

    @Produces
    @WithTopicConfiguration
    private KafkaSender buildSender(InjectionPoint ip) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends KafkaTopicConfiguration> configClass = ip.getAnnotated().getAnnotation(WithTopicConfiguration.class).configName();
        KafkaTopicConfiguration<?> config = configClass.getConstructor().newInstance();

        return new KafkaSender<>(config, serviceName);
    }

}
