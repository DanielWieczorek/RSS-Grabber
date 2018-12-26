package de.wieczorek.rss.insight.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.recalculation.Recalculation;
import de.wieczorek.rss.core.recalculation.RecalculationStatusDao;
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

    @Inject
    private RecalculationStatusDao recalculationDao;

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
	Recalculation recalculation = new Recalculation();
	recalculation.setLastDate(LocalDateTime.of(1900, 1, 1, 1, 1));
	recalculationDao.deleteAll();
	recalculationDao.create(recalculation);
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
