package de.wieczorek.rss.insight.ui;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.insight.business.RssSentimentNeuralNetwork;
import de.wieczorek.rss.insight.persistence.RssEntryDao;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @Inject
    private RssEntryDao dao;

    @Inject
    private RssSentimentNeuralNetwork network;

    public void trainNeuralNetwork() {
	logger.info("get all classified");

	network.train(dao.findAllClassified());
    }

}
