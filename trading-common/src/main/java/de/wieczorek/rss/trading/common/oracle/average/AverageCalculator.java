package de.wieczorek.rss.trading.common.oracle.average;

import java.util.List;

public interface AverageCalculator {

    double calculate(List<Double> values);
}
