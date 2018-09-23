package de.wieczorek.rss.advisor.types.rss;

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
