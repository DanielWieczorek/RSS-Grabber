package de.wieczorek.rss.trading.common.oracle.average;

import java.util.List;

public class MaxAverageCalculator implements AverageCalculator {
    @Override
    public double calculate(List<Double> values) {
        return values.stream().max(Double::compareTo).orElse(0.0);
    }
}
