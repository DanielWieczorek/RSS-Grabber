package de.wieczorek.rss.trading.common.oracle.average;

import java.util.List;

public class SimpleAverageCalculator implements AverageCalculator {
    @Override
    public double calculate(List<Double> values) {
        var average = 0.0;
        int t = 1;
        for (Double current : values) {
            average += (current - average) / t;
            ++t;
        }
        return average;
    }
}
