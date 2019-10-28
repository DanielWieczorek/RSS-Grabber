package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Comparison {
    GREATER(0, GreaterComparator::new),
    LOWER(1, LowerComparator::new),
    RISE_ABOVE(2, RiseAboveComparator::new),
    FALL_BELOW(3, FallBelowComparator::new),
    NEVER_MATCH(4, (threshold) -> new NeverMatchComparator()),
    ALWAYS_MATCH(5, (threshold) -> new AlwaysMatchComparator());


    private final int index;

    private final Function<Integer, Predicate<Double>> comparatorBuilder;

    Comparison(int index, Function<Integer, Predicate<Double>> comparatorBuilder) {
        this.index = index;
        this.comparatorBuilder = comparatorBuilder;
    }

    public static Comparison getValueForIndex(int index) {
        for (Comparison comp : values()) {
            if (comp.index == index) {
                return comp;
            }
        }
        throw new RuntimeException("invalid index " + index);
    }

    public Function<Integer, Predicate<Double>> getComparatorBuilder() {
        return comparatorBuilder;
    }
}
