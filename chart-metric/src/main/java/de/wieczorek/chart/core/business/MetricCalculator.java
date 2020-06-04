package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.BarSeries;

import java.util.Map;
import java.util.function.BiConsumer;

public interface MetricCalculator {

    ChartMetricRecord calculate(BarSeries timeSeries, Map<Integer, BiConsumer<ChartMetricRecord, Double>> config);
}
