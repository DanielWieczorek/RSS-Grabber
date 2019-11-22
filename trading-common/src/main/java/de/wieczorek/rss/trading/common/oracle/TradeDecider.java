package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.StateEdge;

import java.util.function.Function;
import java.util.function.Predicate;

public class TradeDecider {

    private TradeConfiguration configuration;

    private Function<Account, Double> currencyAmountGetter;

    private Predicate<Double> comparator;


    TradeDecider(TradeConfiguration configuration, Function<Account, Double> currencyAmountGetter) {
        comparator = configuration.getComparison().getComparatorBuilder().apply(configuration.getThreshold());
        this.configuration = configuration;
        this.currencyAmountGetter = currencyAmountGetter;
    }

    public boolean decide(StateEdge snapshot) {
        if (currencyAmountGetter.apply(snapshot.getAccount()) == 0) {
            return false;
        }
        if (!isTimeSpanSufficient(snapshot)) {
            return false;
        }

        return comparator.test(calculateAverage(snapshot));

    }

    private boolean isTimeSpanSufficient(StateEdge snapshot) {
        int end = snapshot.getPartsEndIndex();

        return (end - configuration.getAverageTime()) >= 0;
    }


    private double calculateAverage(StateEdge snapshot) {
        int end = snapshot.getPartsEndIndex();

        int start = Math.max(0, end - configuration.getAverageTime());

        int averageNumbers = 0;
        double average = 0;
        for (int i = start; i < end; i++) {
            if (snapshot.getAllStateParts().get(i).getMetricsSentiment() != null) {
                averageNumbers++;
                average += snapshot.getAllStateParts().get(i).getMetricsSentiment().getPrediction();
            }
        }
        average /= (double) averageNumbers;
        return average;
    }
}
