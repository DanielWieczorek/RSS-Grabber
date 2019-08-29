package de.wieczorek.rss.trading.types;

import java.util.List;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.advisor.types.DeltaChartEntry;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;

public class StateEdgePart {
    private TradingEvaluationResult sentiment;
    private DeltaChartEntry chartEntry;
    private List<ChartMetricRecord> metricsRecord;

    public DeltaChartEntry getChartEntry() {
        return chartEntry;
    }

    public void setChartEntry(DeltaChartEntry chartEntry) {
        this.chartEntry = chartEntry;
    }

    public TradingEvaluationResult getSentiment() {
        return sentiment;
    }

    public void setSentiment(TradingEvaluationResult sentiment) {
        this.sentiment = sentiment;
    }

    public List<ChartMetricRecord> getMetricsRecord() {
        return metricsRecord;
    }

    public void setMetricsRecord(List<ChartMetricRecord> metricsRecord) {
        this.metricsRecord = metricsRecord;
    }

}