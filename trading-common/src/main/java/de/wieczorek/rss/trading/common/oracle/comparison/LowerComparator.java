package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class LowerComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(LowerComparator.class);

    private int threshold;

    public LowerComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.second < threshold;
        logger.debug(aDouble.second + " < " + threshold + " = " + result);
        return result;
    }
}
