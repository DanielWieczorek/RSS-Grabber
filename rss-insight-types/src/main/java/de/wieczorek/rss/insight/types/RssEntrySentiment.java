package de.wieczorek.rss.insight.types;

import de.wieczorek.rss.classification.types.RssEntry;

public class RssEntrySentiment {

    private RssEntry entry;

    private double positiveProbability;
    private double negativeProbability;

    public RssEntry getEntry() {
        return entry;
    }

    public void setEntry(RssEntry entry) {
        this.entry = entry;
    }

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
