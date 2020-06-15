package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NetInputItem {
    private Map<LocalDateTime, List<ChartMetricRecord>> inputChartMetrics;

    private Map<LocalDateTime, ChartEntry> chartEntries;

    private List<LocalDateTime> dates;

    private int startIndex;

    private int endIndex;

    private double outputDelta;

    private LocalDateTime date;


    public Map<LocalDateTime, List<ChartMetricRecord>> getInputChartMetrics() {
        return inputChartMetrics;
    }

    public void setInputChartMetrics(Map<LocalDateTime, List<ChartMetricRecord>> inputChartMetrics) {
        this.inputChartMetrics = inputChartMetrics;
    }

    public Map<LocalDateTime, ChartEntry> getChartEntries() {
        return chartEntries;
    }

    public void setChartEntries(Map<LocalDateTime, ChartEntry> chartEntries) {
        this.chartEntries = chartEntries;
    }

    public List<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(List<LocalDateTime> dates) {
        this.dates = dates;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getOutputDelta() {
        return outputDelta;
    }

    public void setOutputDelta(double outputDelta) {
        this.outputDelta = outputDelta;
    }

}