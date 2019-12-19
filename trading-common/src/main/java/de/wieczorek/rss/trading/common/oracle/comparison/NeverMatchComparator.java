package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class NeverMatchComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(NeverMatchComparator.class);

    public NeverMatchComparator() {
    }

    @Override

    public boolean test(ComparatorInput aDouble) {
        logger.debug("evaluated to false");
        return false;
    }
}
