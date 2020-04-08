package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class RangeComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(RangeComparator.class);

    private ComparatorConfiguration config;

    public RangeComparator(ComparatorConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.first < aDouble.second - config.threshold
                && aDouble.first > aDouble.second - config.threshold - config.range;
        logger.debug(aDouble.first + " < " + aDouble.second + " - " + config.threshold + " AND " +
                aDouble.first + " > " + aDouble.second + " - " + config.threshold + " - " + config.range + " = " + result);

        return result;
    }
}
