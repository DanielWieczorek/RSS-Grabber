package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.ChartEntryDao;
import de.wieczorek.core.jgroups.RestInfoSender;
import de.wieczorek.core.kafka.KafkaTopicSubscriberManager;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(RestInfoSender.class);

    @Inject
    private RecurrentTaskManager timer;


    @Inject
    private ChartEntryDao dao;

    @Inject
    private KafkaTopicSubscriberManager kafkaManager;

    @Override
    public void start() {
        logger.info("started");
        timer.start();
        kafkaManager.start();
    }

    @Override
    public void stop() {
        logger.info("stopped");
        timer.stop();
        kafkaManager.stop();
    }

    public List<ChartEntry> getAll() {
        return dao.findAll();
    }


    public List<ChartEntry> getStartingFrom(LocalDateTime startDate) {
        return dao.findAfter(startDate);
    }
}
