package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.util.Map;
import java.util.function.BiConsumer;

class DurationFieldMappingHolder {
    static Map<Integer, BiConsumer<ChartMetricRecord, Double>> configs = Map.of(
            1, ChartMetricRecord::setValue1min,
            5, ChartMetricRecord::setValue5min,
            15, ChartMetricRecord::setValue15min,
            30, ChartMetricRecord::setValue30min,
            60, ChartMetricRecord::setValue60min,
            2 * 60, ChartMetricRecord::setValue2hour,
            6 * 60, ChartMetricRecord::setValue6hour,
            12 * 60, ChartMetricRecord::setValue12hour,
            24 * 60, ChartMetricRecord::setValue24hour

    );
}
