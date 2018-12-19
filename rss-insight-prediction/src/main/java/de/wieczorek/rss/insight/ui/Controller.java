package de.wieczorek.rss.insight.ui;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.insight.business.RssSentimentNeuralNetworkPredictor;
import de.wieczorek.rss.insight.business.RssWord2VecNetwork;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.RssEntrySentiment;
import de.wieczorek.rss.insight.types.RssEntrySentimentSummary;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @Inject
    private RssSentimentNeuralNetworkPredictor network;

    @Inject
    private RssWord2VecNetwork vec;

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private SentimentAtTimeDao dao;

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

    public void recalculate() {
	stop();
	int numberOfRecalculations = 120;
	List<RssEntry> input = ClientBuilder.newClient().target("http://localhost:8020/rss-entries")
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		});

	vec.train(input);
	AtomicInteger j = new AtomicInteger(0);
	List<RssEntrySentiment> sentimentList = input.stream().map(network::predict).peek(x -> {
	    System.out.println(j.incrementAndGet());
	}).collect(Collectors.toList());

	for (int i = 0; i < input.size() - 24 * 60; i++) {
	    List<RssEntry> partition = input.subList(i, i + 24 * 60);
	    List<RssEntrySentiment> sentimentSubList = sentimentList.subList(i, i + 24 * 60);

	    double positiveSum = sentimentSubList.stream().mapToDouble(RssEntrySentiment::getPositiveProbability).sum()
		    / sentimentList.size();
	    double negativeSum = sentimentSubList.stream().mapToDouble(RssEntrySentiment::getNegativeProbability).sum()
		    / sentimentList.size();

	    SentimentEvaluationResult result = new SentimentEvaluationResult();
	    RssEntrySentimentSummary summary = new RssEntrySentimentSummary();
	    summary.setPositiveProbability(positiveSum);
	    summary.setNegativeProbability(negativeSum);
	    result.setSummary(summary);
	    result.setSentiments(sentimentList);

	    SentimentAtTime entity = new SentimentAtTime();
	    entity.setPositiveProbability(positiveSum);
	    entity.setNegativeProbability(negativeSum);
	    entity.setSentimentTime(
		    LocalDateTime.ofInstant(partition.get(partition.size() - 1).getPublicationDate().toInstant(),
			    ZoneId.of(TimeZone.getDefault().getID())));
	    dao.update(entity);

	    System.out.println(i);
	}
	start();
    }

    @Override
    public void start() {
	timer.start();
    }

    @Override
    public void stop() {
	timer.stop();
    }

    public List<SentimentAtTime> getAllSentimentAtTime() {
	return dao.findAll();
    }

}
