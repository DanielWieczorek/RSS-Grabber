package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class DiffBelowComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(DiffBelowComparator.class);

    private int threshold;

    public DiffBelowComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.second - aDouble.second < threshold;
        logger.debug(aDouble.second + " - " + aDouble.first + " < " + threshold + " = " + result);
        return result;
    }
}
