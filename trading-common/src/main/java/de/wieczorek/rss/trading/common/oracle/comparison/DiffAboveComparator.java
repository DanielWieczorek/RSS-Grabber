package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class DiffAboveComparator implements Predicate<ComparatorInput> {

    private static final Logger logger = LoggerFactory.getLogger(DiffAboveComparator.class);

    private int threshold;

    public DiffAboveComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.second - aDouble.first > threshold;
        logger.debug(aDouble.second + " - " + aDouble.first + " > " + threshold + " = " + result);
        return result;
    }
}
