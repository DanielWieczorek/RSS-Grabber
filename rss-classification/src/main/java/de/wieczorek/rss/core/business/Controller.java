package de.wieczorek.rss.core.business;

import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.classification.types.ClassificationStatistics;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;
import de.wieczorek.rss.core.persistence.RssEntryDao;
import de.wieczorek.rss.types.ui.RssDataCollectionLocalRestCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private RssEntryDao dao;

    @Inject
    private RssDataCollectionLocalRestCaller rssDataCollectionCaller;

    public List<ClassifiedRssEntry> readUnclassifiedEntries() {
        logger.info("get all unclassified");
        Date newest = dao.findNewestEntry();
        if (newest == null) {
            newest = new Date(0);
        }

        List<ClassifiedRssEntry> newEntries = new ArrayList<>();
        newEntries.addAll(convertAll(rssDataCollectionCaller.allEntries()));

        if (!newEntries.isEmpty()) {
            List<String> existingEntryKeys = dao
                    .findAll(newEntries.stream().map(ClassifiedRssEntry::getURI).collect(Collectors.toList())).stream()
                    .map(ClassifiedRssEntry::getURI).collect(Collectors.toList());
            newEntries.removeAll(newEntries.stream().filter(entry -> existingEntryKeys.contains(entry.getURI()))
                    .collect(Collectors.toList()));
            logger.info("persisting " + newEntries.size() + " entries");
            dao.persistAll(newEntries);
        }

        return dao.findAllUnclassified(100);
    }

    private Collection<? extends ClassifiedRssEntry> convertAll(List<de.wieczorek.rss.types.RssEntry> allEntries) {
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

    public void updateClassification(ClassifiedRssEntry entry) {
        ClassifiedRssEntry found = dao.find(entry);
        found.setClassification(entry.getClassification());
        dao.persist(found);
    }

    public List<ClassifiedRssEntry> readClassfiedEntries() {
        return dao.findAllClassified();
    }

    public ClassificationStatistics getClassificationStatistics() {
        ClassificationStatistics statistics = new ClassificationStatistics();
        statistics.setClassified(dao.countClassifiedEntries());
        statistics.setUnclassified(dao.countUnclassifiedEntries());

        return statistics;
    }
}
