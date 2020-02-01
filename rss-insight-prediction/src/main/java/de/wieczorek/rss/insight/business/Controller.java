package de.wieczorek.rss.insight.business;

import de.wieczorek.core.recalculation.Recalculation;
import de.wieczorek.core.recalculation.RecalculationStatusDao;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.RssEntrySentiment;
import de.wieczorek.rss.insight.types.RssEntrySentimentSummary;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import de.wieczorek.rss.types.ui.RssDataCollectionLocalRestCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private RssSentimentNeuralNetworkPredictor network;

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private SentimentAtTimeDao dao;

    @Inject
    private RecalculationStatusDao recalculationDao;

    @Inject
    private RssDataCollectionLocalRestCaller rssDataCollectionCaller;


    public SentimentEvaluationResult predict() {
        List<ClassifiedRssEntry> input = convertAll(rssDataCollectionCaller.entriesFromLast24h());

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

    private List<ClassifiedRssEntry> convertAll(List<de.wieczorek.rss.types.RssEntry> allEntries) {
        return allEntries.stream().map(entry -> {
            ClassifiedRssEntry newEntry = new ClassifiedRssEntry();
            newEntry.setCreatedAt(entry.getCreatedAt());
            newEntry.setDescription(entry.getDescription());
            newEntry.setFeedUrl(entry.getFeedUrl());
            newEntry.setHeading(entry.getHeading());
            newEntry.setPublicationDate(entry.getPublicationDate());
            newEntry.setURI(entry.getURI());
            return newEntry;
        }).collect(Collectors.toList());
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
