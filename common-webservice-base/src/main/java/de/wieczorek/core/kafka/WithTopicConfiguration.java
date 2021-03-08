package de.wieczorek.core.kafka;


import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface WithTopicConfiguration {

    @Nonbinding
    Class<? extends KafkaTopicConfiguration> configName() default DefaultKafkaTopicConfiguration.class;
}
