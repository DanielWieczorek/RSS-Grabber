package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class FallingComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(FallingComparator.class);


    public FallingComparator(int threshold) {
    }

    @Override

    public boolean test(ComparatorInput aDouble) {
        boolean result = aDouble.first > aDouble.second;
        logger.debug(aDouble.first + " > " + aDouble.second + " = " + result);

        return result;
    }
}
