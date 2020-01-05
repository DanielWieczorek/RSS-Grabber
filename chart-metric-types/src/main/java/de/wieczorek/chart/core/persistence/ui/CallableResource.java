package de.wieczorek.chart.core.persistence.ui;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.util.List;

public interface CallableResource {
    List<ChartMetricRecord> metricAll();

    List<ChartMetricRecord> metric24h();

    List<ChartMetricRecord> metricNow();

    void recompute();
}
