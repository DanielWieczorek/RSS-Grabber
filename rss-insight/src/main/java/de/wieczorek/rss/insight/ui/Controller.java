package de.wieczorek.rss.insight.ui;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.insight.business.RssSentimentNeuralNetwork;
import de.wieczorek.rss.insight.business.RssWord2VecNetwork;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.RssEntrySentiment;
import de.wieczorek.rss.insight.types.RssEntrySentimentSummary;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    private boolean isStarted = false;
    @Inject
    private RssSentimentNeuralNetwork network;

    @Inject
    private RssWord2VecNetwork vec;

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private SentimentAtTimeDao dao;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
	start();
    }

    public void trainNeuralNetwork() {
	logger.info("get all classified");
	timer.stop();

	List<RssEntry> data = ClientBuilder.newClient().target("http://localhost:10020/classified")
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		});

	vec.train(data);
	network.train(data, 25);

	timer.start();

    }

    public SentimentEvaluationResult predict() {
	List<RssEntry> input = ClientBuilder.newClient().target("http://localhost:8020/rss-entries/24h")
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		});

	vec.train(input);
	List<RssEntrySentiment> sentimentList = input.stream().map(network::predict).collect(Collectors.toList());

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

    public void start() {
	timer.start();
	isStarted = true;
    }

    public List<SentimentAtTime> getAllSentimentAtTime() {
	return dao.findAll();
    }

    public void stop() {
	timer.stop();
	isStarted = false;
    }

    public boolean isStarted() {
	return isStarted;
    }

}
