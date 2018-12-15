package de.wieczorek.rss.trading.ui;

import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;

public class StateEdgePart {
    private TradingEvaluationResult sentiment;
    private DeltaChartEntry chartEntry;

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
}