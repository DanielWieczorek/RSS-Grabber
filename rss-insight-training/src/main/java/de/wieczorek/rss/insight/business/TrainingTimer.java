package de.wieczorek.rss.insight.business;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 0, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LogManager.getLogger(TrainingTimer.class.getName());

    @Inject
    private RssSentimentNeuralNetworkTrainer network;

    @Inject
    private RssWord2VecNetwork vec;

    public TrainingTimer() {

    }

    @Override
    public void run() {
	try {

	    logger.info("get all classified");
	    List<RssEntry> data = ClientBuilder.newClient().target("http://wieczorek.io:10020/classified")
		    .request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		    });

	    vec.train(data);
	    network.train(data, 500);
	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	}
    }

}
