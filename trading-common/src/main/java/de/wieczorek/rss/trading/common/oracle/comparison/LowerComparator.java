package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Predicate;

public class LowerComparator implements Predicate<Double> {

    private int threshold;

    public LowerComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override

    public boolean test(Double aDouble) {
        return aDouble < threshold;
    }
}
