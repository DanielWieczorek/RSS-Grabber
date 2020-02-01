package de.wieczorek.rss.insight.types;

import de.wieczorek.rss.classification.types.ClassifiedRssEntry;

public class RssEntrySentiment {

    private ClassifiedRssEntry entry;

    private double positiveProbability;
    private double negativeProbability;

    public ClassifiedRssEntry getEntry() {
        return entry;
    }

    public void setEntry(ClassifiedRssEntry entry) {
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
