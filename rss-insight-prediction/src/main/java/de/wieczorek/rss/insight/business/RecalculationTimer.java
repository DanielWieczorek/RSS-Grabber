package de.wieczorek.rss.insight.business;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.recalculation.AbstractRecalculationTimer;
import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.RssEntrySentiment;
import de.wieczorek.rss.insight.types.RssEntrySentimentSummary;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class RecalculationTimer extends AbstractRecalculationTimer {

	@Inject
    private RssSentimentNeuralNetworkPredictor network;

    @Inject
    private SentimentAtTimeDao tradingDao;

    @Inject
    private RssWord2VecNetwork vec;

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {

	List<RssEntry> input = ClientBuilder.newClient().target("http://localhost:8020/rss-entries")
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		});

	vec.train(input);
	List<RssEntrySentiment> sentimentList = input.stream().map(network::predict).collect(Collectors.toList());

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
	    tradingDao.upsert(entity);

	}
	return null;
    }

}
