package de.wieczorek.rss.trading.types;

import de.wieczorek.chart.advisor.types.DeltaChartEntry;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.util.List;

public class StateEdgePart {
    private de.wieczorek.rss.advisor.types.TradingEvaluationResult sentiment;
    private DeltaChartEntry chartEntry;
    private TradingEvaluationResult metricsSentiment;

    public DeltaChartEntry getChartEntry() {
        return chartEntry;
    }

    public void setChartEntry(DeltaChartEntry chartEntry) {
        this.chartEntry = chartEntry;
    }

    public de.wieczorek.rss.advisor.types.TradingEvaluationResult getSentiment() {
        return sentiment;
    }

    public void setSentiment(de.wieczorek.rss.advisor.types.TradingEvaluationResult sentiment) {
        this.sentiment = sentiment;
    }

    public TradingEvaluationResult getMetricsSentiment() {
        return metricsSentiment;
    }

    public void setMetricsSentiment(TradingEvaluationResult metricsSentiment) {
        this.metricsSentiment = metricsSentiment;
    }

}