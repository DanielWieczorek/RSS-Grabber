package de.wieczorek.rss.trading.common.oracle;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum Operator {
    AND(0, Predicate::and),
    OR(1, Predicate::or),
    XOR(2, (currentDecider, parameterDecider) -> (t) -> currentDecider.test(t) != parameterDecider.test(t)),
    NAND(3, (currentDecider, parameterDecider) -> currentDecider.and(parameterDecider).negate()),
    NOR(4, (currentDecider, parameterDecider) -> currentDecider.or(parameterDecider).negate()),
    IMPLICATION(5, (currentDecider, parameterDecider) -> currentDecider.negate().or(parameterDecider).negate());


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
