package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Predicate;

public class NeverMatchComparator implements Predicate<Double> {

    public NeverMatchComparator() {
    }

    @Override

    public boolean test(Double aDouble) {
     return false;
    }
}
