package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Predicate;

public class FallBelowComparator implements Predicate<Double> {

    private int threshold;
    private double lastValue = 0;

    public FallBelowComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override

    public boolean test(Double aDouble) {
        boolean result = aDouble < threshold && lastValue >= threshold;
        lastValue = aDouble;
        return result;
    }
}
