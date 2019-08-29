package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NetInputItem {
    private Map<ChartEntry, List<ChartMetricRecord>> inputChartMetrics;

    public List<ChartEntry> getChartEntries() {
        return chartEntries;
    }

    public void setChartEntries(List<ChartEntry> chartEntries) {
        this.chartEntries = chartEntries;
    }

    private List<ChartEntry> chartEntries;

    private double outputDelta;

    private LocalDateTime date;


    public double getOutputDelta() {
        return outputDelta;
    }

    public void setOutputDelta(double outputDelta) {
        this.outputDelta = outputDelta;
    }

    public Map<ChartEntry, List<ChartMetricRecord>> getInputChartMetrics() {
        return inputChartMetrics;
    }

    public void setInputChartMetrics(Map<ChartEntry, List<ChartMetricRecord>> inputChartMetrics) {
        this.inputChartMetrics = inputChartMetrics;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}