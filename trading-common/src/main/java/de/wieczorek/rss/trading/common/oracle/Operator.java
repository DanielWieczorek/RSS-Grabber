package de.wieczorek.rss.trading.common.oracle;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum Operator {
    AND(0, Predicate::and),
    OR(1, Predicate::or);


    private final int index;

    private final BiFunction<Predicate<OracleInput>, Predicate<OracleInput>, Predicate<OracleInput>> combinationFunction;

    Operator(int index, BiFunction<Predicate<OracleInput>, Predicate<OracleInput>, Predicate<OracleInput>> combinationFunction) {
        this.index = index;
        this.combinationFunction = combinationFunction;
    }


    public static Operator getValueForIndex(int index) {
        for (Operator op : values()) {
            if (op.index == index) {
                return op;
            }
        }
        throw new RuntimeException("invalid index " + index);
    }

    public BiFunction<Predicate<OracleInput>, Predicate<OracleInput>, Predicate<OracleInput>> getCombinationFunction() {
        return combinationFunction;
    }
}
