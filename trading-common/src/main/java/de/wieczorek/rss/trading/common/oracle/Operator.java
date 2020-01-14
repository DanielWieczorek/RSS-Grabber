package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.StateEdge;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum Operator {
    AND(0, (currentDecider, parameterDecider) -> currentDecider.and(parameterDecider)),
    OR(1, (currentDecider, parameterDecider) -> currentDecider.or(parameterDecider)),
    XOR(2, (currentDecider, parameterDecider) -> (t) -> currentDecider.test(t) != parameterDecider.test(t));


    private final int index;

    private final BiFunction<Predicate<StateEdge>, Predicate<StateEdge>, Predicate<StateEdge>> combinationFunction;

    Operator(int index, BiFunction<Predicate<StateEdge>, Predicate<StateEdge>, Predicate<StateEdge>> combinationFunction) {
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

    public BiFunction<Predicate<StateEdge>, Predicate<StateEdge>, Predicate<StateEdge>> getCombinationFunction() {
        return combinationFunction;
    }
}
