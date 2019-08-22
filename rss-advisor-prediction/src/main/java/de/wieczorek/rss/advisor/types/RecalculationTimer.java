package de.wieczorek.rss.advisor.types;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.persistence.TradingEvaluationResultDao;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.recalculation.AbstractRecalculationTimer;
import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class RecalculationTimer extends AbstractRecalculationTimer {
	private static final Logger logger = LoggerFactory.getLogger(RecalculationTimer.class);


	private static final int NUMBER_OF_ENTRIES = 300;

    @Inject
    private TradingNeuralNetworkPredictor nn;
    @Inject
    private TradingEvaluationResultDao tradingDao;

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {

	List<SentimentAtTime> sentiments = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:11020/sentiment-at-time").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<SentimentAtTime>>() {
		});

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv/").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});

		logger.debug("calculating for " + chartEntries.size() + "entries");

	DataPreparator preparator = new DataPreparator().withChartData(chartEntries);
	int startIndex = 0;
	for (int i = 0; i < sentiments.size(); i++) {
	    if (sentiments.get(i).getSentimentTime().isEqual(startDate)) {
		startIndex = i + 1;
		break;
	    }
	}
	int i = 0;
	for (i = 0; i < NUMBER_OF_ENTRIES && i + startIndex < sentiments.size(); i++) {
	    SentimentAtTime sentiment = sentiments.get(i + startIndex);
	    NetInputItem networkInput = preparator.getDataForSentiment(sentiment);
	    if (networkInput != null) {
		TradingEvaluationResult result = nn.predict(networkInput);
		result.setCurrentTime(sentiment.getSentimentTime());
		result.setTargetTime(sentiment.getSentimentTime().plusMinutes(preparator.getOffsetMinutes()));

		tradingDao.upsert(result);
			logger.debug( "calculating for date " + sentiment.getSentimentTime());
	    }
	}

	if (startIndex + NUMBER_OF_ENTRIES < sentiments.size()) {
	    return sentiments.get(i + startIndex).getSentimentTime();
	} else {
	    return null;
	}

    }

}
