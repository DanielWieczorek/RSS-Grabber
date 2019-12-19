package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class FallBelowComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(FallBelowComparator.class);

    private int threshold;

    public FallBelowComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override

    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.second < threshold && aDouble.first >= threshold;
        logger.debug(aDouble.second + " < " + threshold + " and " + aDouble.first + " >= " + threshold + " = " + result);
        return result;
    }
}
