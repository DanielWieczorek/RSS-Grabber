package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Predicate;

public class GreaterComparator implements Predicate<Double> {

    private int threshold;

    public GreaterComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override

    public boolean test(Double aDouble) {
        return aDouble > threshold;
    }
}
