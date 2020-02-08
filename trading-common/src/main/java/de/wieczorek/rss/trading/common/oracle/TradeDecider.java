package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.average.AverageCalculator;
import de.wieczorek.rss.trading.common.oracle.comparison.ComparatorInput;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.StateEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TradeDecider implements Predicate<OracleInput> {
    private static final Logger logger = LoggerFactory.getLogger(TradeDecider.class);

    private TradeConfiguration configuration;

    private Function<Account, Double> currencyAmountGetter;

    private Predicate<ComparatorInput> comparator;

    private Supplier<AverageCalculator> calculatorSupplier;

    private Function<OracleInput, List<Double>> valueExtractor1;
    private Function<OracleInput, List<Double>> valueExtractor2;


    TradeDecider(TradeConfiguration configuration, Function<Account, Double> currencyAmountGetter) {
        comparator = configuration.getComparison().getComparatorBuilder().apply(configuration.getThreshold());
        this.configuration = configuration;
        this.currencyAmountGetter = currencyAmountGetter;
        this.calculatorSupplier = configuration.getAverageType().getAverageCalculatorBuilder();
        this.valueExtractor1 = configuration.getValuesSource().getValueExtractor().getValueExtractor1();
        this.valueExtractor2 = configuration.getValuesSource().getValueExtractor().getValueExtractor1();
    }

    public boolean test(OracleInput input) {
        if (currencyAmountGetter.apply(input.getStateEdge().getAccount()) == 0) {
            logger.debug("insufficient funds");
            return false;
        }
        if (!isTimeSpanSufficient(input.getStateEdge())) {
            logger.debug("time span too short");
            return false;
        }

        return comparator.test(buildInput(input));

    }

    private ComparatorInput buildInput(OracleInput input) {
        ComparatorInput result = new ComparatorInput();
        result.first = calculateAverage(valueExtractor1, input, configuration.getOffset());
        result.second = calculateAverage(valueExtractor2, input, 0);
        return result;
    }

    private boolean isTimeSpanSufficient(StateEdge snapshot) {
        int end = snapshot.getPartsEndIndex();
        boolean evaluation = (end - configuration.getAverageTime() - configuration.getOffset()) >= 0;
        logger.debug("checking timespans: " + end + " - " + configuration.getAverageTime() + " - " + configuration.getOffset() + " >= 0 = " + evaluation);

        return evaluation;
    }


    private double calculateAverage(Function<OracleInput, List<Double>> valueExtractor, OracleInput input, int negativeOffset) {
        int end = input.getStateEdge().getPartsEndIndex() - negativeOffset;

        int start = Math.max(0, end - configuration.getAverageTime());

        OracleInput updatedInput = new OracleInput();
        updatedInput.setState(input.getState());
        StateEdge edge = new StateEdge();
        edge.setAllStateParts(input.getStateEdge().getAllStateParts().subList(start, end));
        edge.setAccount(input.getStateEdge().getAccount());
        edge.setPartsStartIndex(input.getStateEdge().getPartsStartIndex());
        edge.setPartsEndIndex(input.getStateEdge().getPartsEndIndex());

        updatedInput.setStateEdge(edge);
        updatedInput.getStateEdge().setAllStateParts(input.getStateEdge().getAllStateParts().subList(start, end));

        List<Double> predictions = valueExtractor.apply(updatedInput);

        return calculatorSupplier.get().calculate(predictions);
    }
}
