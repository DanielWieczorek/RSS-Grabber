package de.wieczorek.rss.insight.business;

import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;
import de.wieczorek.rss.classification.types.ui.RssAdvisorRemoteRestCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private RssSentimentNeuralNetworkTrainer network;

    @Inject
    private RssWord2VecNetwork vec;

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private Word2VecDao word2VecDao;

    @Inject
    private DataGenerator generator;

    @Inject
    private RssAdvisorRemoteRestCaller rssAdvisorCaller;

    public void trainNeuralNetwork() {
        logger.info("get all classified");
        timer.stop();

        List<ClassifiedRssEntry> data = rssAdvisorCaller.classified();

        vec.train(data);
        network.train(generator, 25);

        timer.start();

    }

    @Override
    public void start() {
        timer.start();
    }

    @Override
    public void stop() {
        timer.stop();
    }
}
