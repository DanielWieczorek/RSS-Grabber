package de.wieczorek.rss.trading.common.oracle.average;

import java.util.List;

public class ExponentialMovingAverageCalculator implements AverageCalculator {

    private static final int PERIOD_LENGTH = 10;

    private SimpleAverageCalculator simpleAverage = new SimpleAverageCalculator();

    @Override
    public double calculate(List<Double> values) {
        if (values.size() <= PERIOD_LENGTH) {
            return simpleAverage.calculate(values);
        }

        double currentAverage = simpleAverage.calculate(values.subList(0, PERIOD_LENGTH - 1));
        double weightingFactor = 2.0 / (PERIOD_LENGTH + 1);

        for (int i = PERIOD_LENGTH; i < values.size(); i++) {
            currentAverage = (values.get(i) - currentAverage) * weightingFactor + currentAverage;
        }
        return currentAverage;
    }
}
