package de.wieczorek.rss.insight.types.ui;

import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

import java.util.List;

public interface CallableResource {
    SentimentEvaluationResult now();

    List<SentimentAtTime> all();

}
