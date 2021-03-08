package de.wieczorek.rss.insight.business;

import de.wieczorek.core.kafka.KafkaSender;
import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.RssSentimentTopicConfiguration;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import de.wieczorek.rss.types.RssEntry;
import de.wieczorek.rss.types.RssEntryTopicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@ApplicationScoped
@EntityManagerContext
@WithTopicConfiguration(configName = RssEntryTopicConfiguration.class)
public class KafkaReceiver implements Consumer<RssEntry> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @Inject
    private SentimentAtTimeDao dao;

    @Inject
    private Controller controller;

    @Inject
    @WithTopicConfiguration(configName = RssSentimentTopicConfiguration.class)
    private KafkaSender<Object> sender;

    @Override
    public void accept(RssEntry entry) {
        try {
            logger.info("predicting");
            SentimentEvaluationResult result = controller.predict();
            SentimentAtTime entity = new SentimentAtTime();
            entity.setPositiveProbability(result.getSummary().getPositiveProbability());
            entity.setNegativeProbability(result.getSummary().getNegativeProbability());
            entity.setSentimentTime(LocalDateTime.now().withSecond(0).withNano(0));
            if (dao.findById(entity.getSentimentTime()) == null) {
                dao.persist(entity);

                sender.send(entity.getSentimentTime().toString(), entity);
            }

        } catch (Exception e) {
            logger.error("error while generating prediction: ", e);
        }
    }

}
