package de.wieczorek.rss.insight.types;

import java.util.List;

public class SentimentEvaluationResult {
    private List<RssEntrySentiment> sentiments;

    private RssEntrySentimentSummary summary;

    public List<RssEntrySentiment> getSentiments() {
        return sentiments;
    }

    public void setSentiments(List<RssEntrySentiment> sentiments) {
        this.sentiments = sentiments;
    }

    public RssEntrySentimentSummary getSummary() {
        return summary;
    }

    public void setSummary(RssEntrySentimentSummary summary) {
        this.summary = summary;
    }

}
