package de.wieczorek.rss.trading.common.oracle.comparison;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Comparison {
    GREATER(0, GreaterComparator::new),
    LOWER(1, LowerComparator::new),
    ALWAYS_MATCH(2, (threshold) -> new AlwaysMatchComparator()),
    RANGE(3, RangeComparator::new);

    private final int index;
    private final Function<ComparatorConfiguration, Predicate<ComparatorInput>> comparatorBuilder;

    Comparison(int index, Function<ComparatorConfiguration, Predicate<ComparatorInput>> comparatorBuilder) {
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

    public int getIndex() {
        return index;
    }

    public Function<ComparatorConfiguration, Predicate<ComparatorInput>> getComparatorBuilder() {
        return comparatorBuilder;
    }
}
