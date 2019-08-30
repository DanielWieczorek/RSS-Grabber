package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.TimeSeries;

public interface MetricCalculator {

    ChartMetricRecord calculate(TimeSeries timeSeries);
}
