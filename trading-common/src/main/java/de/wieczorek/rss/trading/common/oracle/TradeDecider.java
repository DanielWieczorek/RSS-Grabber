package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.average.AverageCalculator;
import de.wieczorek.rss.trading.common.oracle.comparison.ComparatorConfiguration;
import de.wieczorek.rss.trading.common.oracle.comparison.ComparatorInput;
import de.wieczorek.rss.trading.common.oracle.comparison.Comparison;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.StateEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TradeDecider implements Predicate<OracleInput> {
    private static final Logger logger = LoggerFactory.getLogger(TradeDecider.class);

    private TradeConfiguration configuration;

    private Function<Account, Double> currencyAmountGetter;

    private Supplier<AverageCalculator> calculatorSupplier;

    private Function<OracleInput, List<Double>> valueExtractor;

    private List<ValuePoint> valuePoints;

    private List<Function<ComparatorConfiguration, Predicate<ComparatorInput>>> comparatorBuilders;

    private List<Integer> margins;

    private List<Integer> ranges;

    TradeDecider(TradeConfiguration configuration, Function<Account, Double> currencyAmountGetter) {
        this.configuration = configuration;
        this.currencyAmountGetter = currencyAmountGetter;
        this.calculatorSupplier = configuration.getAverageType().getAverageCalculatorBuilder();
        this.valueExtractor = configuration.getValuesSource().getValueExtractor();
        this.valuePoints = configuration.getComparisonPoints();
        this.valuePoints.get(this.valuePoints.size() - 1).setOffset(0);
        this.comparatorBuilders = configuration.getComparisons().stream().map(Comparison::getComparatorBuilder).collect(Collectors.toList());
        this.margins = configuration.getMargins();
        this.ranges = configuration.getRanges();
    }

    public boolean test(OracleInput input) {
        if (currencyAmountGetter.apply(input.getStateEdge().getAccount()) < input.getMinOrder()) {
            logger.debug("insufficient funds");
            return false;
        }
        if (!isTimeSpanSufficient(input.getStateEdge())) {
            logger.debug("time span too short");
            return false;
        }

        for (int i = 0; i < comparatorBuilders.size(); i++) {
            if (!comparatorBuilders.get(i).apply(buildComparatorConfig(margins.get(i), ranges.get(i)))
                    .test(buildInput(input, i, i + 1))) {
                return false;
            }
        }
        return true;
    }

    private ComparatorConfiguration buildComparatorConfig(int threshold, int range) {
        ComparatorConfiguration config = new ComparatorConfiguration();
        config.threshold = threshold;
        config.range = range;
        return config;
    }

    private ComparatorInput buildInput(OracleInput input, int point1Index, int point2Index) {
        ComparatorInput result = new ComparatorInput();
        result.first = calculateAverage(valueExtractor, input, valuePoints.get(point1Index).getAverageTime(), calculateOffset(point1Index));
        result.second = calculateAverage(valueExtractor, input, valuePoints.get(point2Index).getAverageTime(), calculateOffset(point2Index));
        return result;
    }

    private int calculateOffset(int point1Index) {
        int sum = 0;
        for (int i = point1Index; i < valuePoints.size() - 1; i++) {
            sum += valuePoints.get(i).getOffset();
        }

        return sum;
    }

    private boolean isTimeSpanSufficient(StateEdge snapshot) {
        int end = snapshot.getPartsEndIndex();

        int maxValue = 0;
        for (int i = 0; i < valuePoints.size(); i++) {
            ValuePoint point = valuePoints.get(i);
            maxValue = Math.max(maxValue, calculateOffset(point.getOffset()) + point.getAverageTime());
        }

        boolean evaluation = (end - maxValue) >= 0;

        return evaluation;
    }


    private double calculateAverage(Function<OracleInput, List<Double>> valueExtractor,
                                    OracleInput input,
                                    int averageDuration,
                                    int negativeOffset) {
        int end = input.getStateEdge().getPartsEndIndex() - negativeOffset;

        int start = Math.max(0, end - averageDuration);

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
