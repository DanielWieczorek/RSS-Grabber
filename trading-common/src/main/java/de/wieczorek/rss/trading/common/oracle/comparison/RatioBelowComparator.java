package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class RatioBelowComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(RatioBelowComparator.class);

    private int threshold;

    public RatioBelowComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override

    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.second / aDouble.first * 100 < threshold;
        logger.debug(aDouble.second + " / " + aDouble.first + " * 100  > " + threshold + " = " + result);

        return result;
    }
}
