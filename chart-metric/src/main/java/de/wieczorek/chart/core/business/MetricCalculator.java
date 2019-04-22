package de.wieczorek.chart.core.business;

import org.ta4j.core.TimeSeries;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

public interface MetricCalculator {

    ChartMetricRecord calculate(TimeSeries timeSeries);
}
