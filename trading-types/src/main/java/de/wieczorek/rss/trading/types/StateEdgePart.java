package de.wieczorek.rss.trading.types;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;

public class StateEdgePart {
    private de.wieczorek.rss.advisor.types.TradingEvaluationResult sentiment;
    private ChartEntry chartEntry;
    private TradingEvaluationResult metricsSentiment;
    private DeltaChartEntry deltaChartEntry;

    public ChartEntry getChartEntry() {
        return chartEntry;
    }

    public void setChartEntry(ChartEntry chartEntry) {
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

    public DeltaChartEntry getDeltaChartEntry() {
        return deltaChartEntry;
    }

    public void setDeltaChartEntry(DeltaChartEntry deltaChartEntry) {
        this.deltaChartEntry = deltaChartEntry;
    }
}