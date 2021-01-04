package de.wieczorek.rss.trading.common.oracle.average;

import java.util.Comparator;
import java.util.List;

public class MinAverageCalculator implements AverageCalculator {
    @Override
    public double calculate(List<Double> values) {
        return values.stream().min(Double::compareTo).orElse(0.0);
    }
}
