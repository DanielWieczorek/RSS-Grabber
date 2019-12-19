package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.comparison.ComparatorInput;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.StateEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.Predicate;

public class TradeDecider implements Predicate<StateEdge> {
    private static final Logger logger = LoggerFactory.getLogger(TradeDecider.class);

    private TradeConfiguration configuration;

    private Function<Account, Double> currencyAmountGetter;

    private Predicate<ComparatorInput> comparator;


    TradeDecider(TradeConfiguration configuration, Function<Account, Double> currencyAmountGetter) {
        comparator = configuration.getComparison().getComparatorBuilder().apply(configuration.getThreshold());
        this.configuration = configuration;
        this.currencyAmountGetter = currencyAmountGetter;
    }

    public boolean test(StateEdge snapshot) {
        if (currencyAmountGetter.apply(snapshot.getAccount()) == 0) {
            logger.debug("insufficient funds");
            return false;
        }
        if (!isTimeSpanSufficient(snapshot)) {
            logger.debug("time span too short");
            return false;
        }

        return comparator.test(buildInput(snapshot));

    }

    private ComparatorInput buildInput(StateEdge snapshot) {
        ComparatorInput result = new ComparatorInput();
        result.first = calculateAverage(snapshot, configuration.getOffset());
        result.second = calculateAverage(snapshot, 0);
        return result;
    }

    private boolean isTimeSpanSufficient(StateEdge snapshot) {
        int end = snapshot.getPartsEndIndex();

        return (end - configuration.getAverageTime() - configuration.getOffset()) >= 0;
    }


    private double calculateAverage(StateEdge snapshot, int negativeOffset) {
        int end = snapshot.getPartsEndIndex() - negativeOffset;

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
