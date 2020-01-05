package de.wieczorek.rss.insight.business;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.persistence.EntityManagerContext;
import de.wieczorek.rss.core.recalculation.AbstractRecalculationTimer;
import de.wieczorek.rss.core.timer.RecurrentTask;
import de.wieczorek.rss.insight.persistence.SentimentAtTimeDao;
import de.wieczorek.rss.insight.types.RssEntrySentiment;
import de.wieczorek.rss.insight.types.RssEntrySentimentSummary;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import de.wieczorek.rss.types.ui.RssDataCollectionLocalRestCaller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class RecalculationTimer extends AbstractRecalculationTimer {

    @Inject
    private RssSentimentNeuralNetworkPredictor network;

    @Inject
    private SentimentAtTimeDao tradingDao;

    @Inject
    private RssDataCollectionLocalRestCaller rssDataCollectionCaller;

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {

        List<RssEntry> input = convertAll(rssDataCollectionCaller.allEntries());

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

    private List<RssEntry> convertAll(List<de.wieczorek.rss.types.RssEntry> allEntries) {
        return allEntries.stream().map(entry -> {
            RssEntry newEntry = new RssEntry();
            newEntry.setCreatedAt(entry.getCreatedAt());
            newEntry.setDescription(entry.getDescription());
            newEntry.setFeedUrl(entry.getFeedUrl());
            newEntry.setHeading(entry.getHeading());
            newEntry.setPublicationDate(entry.getPublicationDate());
            newEntry.setURI(entry.getURI());
            return newEntry;
        }).collect(Collectors.toList());
    }

}
