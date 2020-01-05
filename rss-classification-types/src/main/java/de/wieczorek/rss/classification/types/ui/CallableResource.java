package de.wieczorek.rss.classification.types.ui;

import de.wieczorek.rss.classification.types.ClassificationStatistics;
import de.wieczorek.rss.classification.types.RssEntry;

import java.util.List;

public interface CallableResource {
    List<RssEntry> find();

    List<RssEntry> classified();

    ClassificationStatistics statistics();
}
