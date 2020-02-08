package de.wieczorek.rss.trading.common.oracle;

import java.util.List;
import java.util.function.Function;

public class ValueExtractorPair {

    private Function<OracleInput, List<Double>> valueExtractor1;
    private Function<OracleInput, List<Double>> valueExtractor2;

    public Function<OracleInput, List<Double>> getValueExtractor1() {
        return valueExtractor1;
    }

    public void setValueExtractor1(Function<OracleInput, List<Double>> valueExtractor1) {
        this.valueExtractor1 = valueExtractor1;
    }

    public Function<OracleInput, List<Double>> getValueExtractor2() {
        return valueExtractor2;
    }

    public void setValueExtractor2(Function<OracleInput, List<Double>> valueExtractor2) {
        this.valueExtractor2 = valueExtractor2;
    }
}
