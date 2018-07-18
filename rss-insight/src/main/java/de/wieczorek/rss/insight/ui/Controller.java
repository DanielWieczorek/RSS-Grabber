package de.wieczorek.rss.insight.ui;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.insight.business.RssEntry;
import de.wieczorek.rss.insight.business.RssEntrySentiment;
import de.wieczorek.rss.insight.business.RssEntrySentimentSummary;
import de.wieczorek.rss.insight.business.RssSentimentNeuralNetwork;
import de.wieczorek.rss.insight.business.SentimentEvaluationResult;
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

	network.train(ClientBuilder.newClient().target("http://localhost:10020/classified")
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		}));
    }

    public SentimentEvaluationResult predict() {
	List<RssEntrySentiment> sentimentList = ClientBuilder.newClient()
		.target("http://localhost:8020/rss-entries/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<RssEntry>>() {
		}).stream().map(network::predict).collect(Collectors.toList());

	double positiveSum = sentimentList.stream().mapToDouble(RssEntrySentiment::getPositiveProbability).sum()
		/ sentimentList.size();
	double negativeSum = sentimentList.stream().mapToDouble(RssEntrySentiment::getNegativeProbability).sum()
		/ sentimentList.size();

	SentimentEvaluationResult result = new SentimentEvaluationResult();
	RssEntrySentimentSummary summary = new RssEntrySentimentSummary();
	summary.setPositiveProbability(positiveSum);
	summary.setNegativeProbability(negativeSum);
	result.setSummary(summary);
	result.setSentiments(sentimentList);
	return result;
    }

}
