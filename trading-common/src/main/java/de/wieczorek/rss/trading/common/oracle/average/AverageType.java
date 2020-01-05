package de.wieczorek.rss.trading.common.oracle.average;

import java.util.function.Supplier;

public enum AverageType {

    EMA(0, ExponentialMovingAverageCalculator::new),
    AVERAGE(1, SimpleAverageCalculator::new);

    private final int index;

    private final Supplier<AverageCalculator> averageCalculatorBuilder;

    AverageType(int index, Supplier<AverageCalculator> averageCalculatorBuilder) {
        this.index = index;
        this.averageCalculatorBuilder = averageCalculatorBuilder;

    }

    public static AverageType getValueForIndex(int index) {
        for (AverageType avg : values()) {
            if (avg.index == index) {
                return avg;
            }
        }
        throw new RuntimeException("invalid index " + index);
    }

    public Supplier<AverageCalculator> getAverageCalculatorBuilder() {
        return averageCalculatorBuilder;
    }
}
