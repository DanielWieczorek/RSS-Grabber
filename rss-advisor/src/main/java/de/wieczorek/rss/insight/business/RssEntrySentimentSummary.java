package de.wieczorek.rss.insight.business;

public class RssEntrySentimentSummary {
    private double positiveProbability;
    private double negativeProbability;

    public double getPositiveProbability() {
	return positiveProbability;
    }

    public void setPositiveProbability(double positiveProbability) {
	this.positiveProbability = positiveProbability;
    }

    public double getNegativeProbability() {
	return negativeProbability;
    }

    public void setNegativeProbability(double negativeProbability) {
	this.negativeProbability = negativeProbability;
    }

}
