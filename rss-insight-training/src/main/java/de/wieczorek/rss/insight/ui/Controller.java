package de.wieczorek.rss.insight.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.insight.business.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.insight.business.RssSentimentNeuralNetworkTrainer;
import de.wieczorek.rss.insight.business.RssWord2VecNetwork;
import de.wieczorek.rss.insight.business.Word2VecDao;

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

    public void trainNeuralNetwork() {
	logger.info("get all classified");
	timer.stop();

	List<RssEntry> data = ClientBuilder.newClient().target("http://wieczorek.io:10020/classified")
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		});

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
