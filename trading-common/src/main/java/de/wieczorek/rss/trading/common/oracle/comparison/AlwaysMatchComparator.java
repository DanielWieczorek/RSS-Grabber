package de.wieczorek.rss.trading.common.oracle.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class AlwaysMatchComparator implements Predicate<ComparatorInput> {
    private static final Logger logger = LoggerFactory.getLogger(AlwaysMatchComparator.class);


    public AlwaysMatchComparator() {
    }

    @Override

    public boolean test(ComparatorInput aDouble) {
        logger.debug("evaluated to true");
        return true;
    }
}
