package de.wieczorek.rss.classification.types.ui;

import de.wieczorek.rss.classification.types.ClassificationStatistics;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;

import java.util.List;

public interface CallableResource {
    List<ClassifiedRssEntry> find();

    List<ClassifiedRssEntry> classified();

    ClassificationStatistics statistics();
}
