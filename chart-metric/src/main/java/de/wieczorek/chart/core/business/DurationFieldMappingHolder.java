package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.util.Map;
import java.util.function.BiConsumer;

public class DurationFieldMappingHolder {
    public static Map<Integer, BiConsumer<ChartMetricRecord, Double>> configs = Map.of(
            1, ChartMetricRecord::setValue1min,
            5, ChartMetricRecord::setValue5min,
            15, ChartMetricRecord::setValue15min,
            30, ChartMetricRecord::setValue30min,
            60, ChartMetricRecord::setValue60min
    );
}
