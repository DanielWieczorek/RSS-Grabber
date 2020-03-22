package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class GreaterComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(GreaterComparator.class);


    private int threshold;

    public GreaterComparator(int threshold) {
        this.threshold = threshold;
    }

    @Override

    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.first > aDouble.second + threshold;
        logger.debug(aDouble.first + " > " + aDouble.second + " + " + threshold + " = " + result);

        return result;
    }
}
