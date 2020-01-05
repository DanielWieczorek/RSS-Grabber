package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.average.AverageCalculator;
import de.wieczorek.rss.trading.common.oracle.comparison.ComparatorInput;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.StateEdge;
import de.wieczorek.rss.trading.types.StateEdgePart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TradeDecider implements Predicate<StateEdge> {
    private static final Logger logger = LoggerFactory.getLogger(TradeDecider.class);

    private TradeConfiguration configuration;

    private Function<Account, Double> currencyAmountGetter;

    private Predicate<ComparatorInput> comparator;

    private Supplier<AverageCalculator> calculatorSupplier;

    private Function<List<StateEdgePart>, List<Double>> valueExtractor;


    TradeDecider(TradeConfiguration configuration, Function<Account, Double> currencyAmountGetter) {
        comparator = configuration.getComparison().getComparatorBuilder().apply(configuration.getThreshold());
        this.configuration = configuration;
        this.currencyAmountGetter = currencyAmountGetter;
        this.calculatorSupplier = configuration.getAverageType().getAverageCalculatorBuilder();
        this.valueExtractor = configuration.getValuesSource().getValueExtractor();
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

        List<Double> predictions = valueExtractor.apply(snapshot.getAllStateParts().subList(start, end));

        return calculatorSupplier.get().calculate(predictions);
    }
}
