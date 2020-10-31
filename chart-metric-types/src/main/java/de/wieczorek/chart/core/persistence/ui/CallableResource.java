package de.wieczorek.chart.core.persistence.ui;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.util.List;

public interface CallableResource {

    List<ChartMetricRecord> metric24h();

    List<ChartMetricRecord> metricNow();
}
