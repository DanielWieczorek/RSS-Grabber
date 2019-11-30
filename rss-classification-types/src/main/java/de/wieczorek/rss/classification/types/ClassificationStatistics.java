package de.wieczorek.rss.classification.types;

public class ClassificationStatistics {

    private long classified;

    private long unclassified;

    public long getClassified() {
        return classified;
    }

    public void setClassified(long classified) {
        this.classified = classified;
    }

    public long getUnclassified() {
        return unclassified;
    }

    public void setUnclassified(long unclassified) {
        this.unclassified = unclassified;
    }
}
