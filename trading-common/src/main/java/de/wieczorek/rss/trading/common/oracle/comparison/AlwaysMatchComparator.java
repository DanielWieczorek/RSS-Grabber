package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Predicate;

public class AlwaysMatchComparator implements Predicate<Double> {

    public AlwaysMatchComparator() {
    }

    @Override

    public boolean test(Double aDouble) {
        return true;
    }
}
