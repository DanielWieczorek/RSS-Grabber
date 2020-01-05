package de.wieczorek.rss.trading.common.oracle.average;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleAverageCalculator implements AverageCalculator {
    @Override
    public double calculate(List<Double> values) {
        return values.stream().collect(Collectors.averagingDouble(x -> x));
    }
}
