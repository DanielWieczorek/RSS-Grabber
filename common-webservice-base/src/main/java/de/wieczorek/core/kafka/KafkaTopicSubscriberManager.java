package de.wieczorek.core.kafka;

import de.wieczorek.core.config.ServiceName;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.timer.RecurrentTaskRunner;
import org.jboss.weld.inject.WeldInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@ApplicationScoped
public class KafkaTopicSubscriberManager {
    private static final Logger logger = LoggerFactory.getLogger(RecurrentTaskManager.class);

    @Inject
    @WithTopicConfiguration
    private WeldInstance<Consumer<?>> tasks;

    private List<RecurrentTaskRunner> runners = new ArrayList<>();

    @Inject
    @ServiceName
    private String serviceName;

    @PostConstruct
    private void init() {
        tasks.handlers().forEach((handler) -> {
            WithTopicConfiguration[] taskAnnotations = handler.getBean().getBeanClass()
                    .getAnnotationsByType(WithTopicConfiguration.class);

            Arrays.asList(taskAnnotations).forEach((annotation) -> {
                try {
                    KafkaTopicConfiguration<?> config = annotation.configName().getConstructor().newInstance();
                    KafkaSubscriber<?> sub = buildSubscriber(config, handler);

                    runners.add(new RecurrentTaskRunner(sub, 1, TimeUnit.SECONDS));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    private <T> KafkaSubscriber<T> buildSubscriber(KafkaTopicConfiguration<T> config, WeldInstance.Handler handler) {
        return new KafkaSubscriber<T>(config, (Consumer<T>) handler.get(), serviceName);
    }

    public void start() {
        logger.debug("triggered start");
        runners.forEach(RecurrentTaskRunner::start);

    }

    public void stop() {
        runners.forEach(RecurrentTaskRunner::stop);

    }
}
