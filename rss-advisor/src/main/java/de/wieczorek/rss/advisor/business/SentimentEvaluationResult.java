package de.wieczorek.rss.advisor.business;

import java.util.List;

import de.wieczorek.rss.advisor.types.rss.RssEntrySentiment;
import de.wieczorek.rss.advisor.types.rss.RssEntrySentimentSummary;

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
