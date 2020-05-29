package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.TimeSeries;

import java.util.Map;
import java.util.function.BiConsumer;

public interface MetricCalculator {

    ChartMetricRecord calculate(TimeSeries timeSeries, Map<Integer, BiConsumer<ChartMetricRecord, Double>> config);
}
