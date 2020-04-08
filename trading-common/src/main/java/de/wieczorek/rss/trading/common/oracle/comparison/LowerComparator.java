package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class LowerComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(LowerComparator.class);

    private ComparatorConfiguration config;

    public LowerComparator(ComparatorConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.first < aDouble.second - config.threshold;
        logger.debug(aDouble.first + " < " + aDouble.second + " - " + config.threshold + " = " + result);

        return result;
    }
}
