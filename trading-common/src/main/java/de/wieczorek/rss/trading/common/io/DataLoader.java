package de.wieczorek.rss.trading.common.io;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.advisor.types.ui.ChartAdvisorRemoteRestCaller;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionRemoteRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.ChartMetricRemoteRestCaller;
import de.wieczorek.rss.advisor.types.ui.RssAdvisorRemoteRestCaller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DataLoader {

    @Inject
    private ChartAdvisorRemoteRestCaller chartAdvisorCaller;

    @Inject
    private ChartMetricRemoteRestCaller chartMetricCaller;

    @Inject
    private RssAdvisorRemoteRestCaller rssAdvisorCaller;

    @Inject
    private ChartDataCollectionRemoteRestCaller chartDataCollectionCaller;

    public List<ChartEntry> loadChartEntries24h() {
        return chartDataCollectionCaller.ohlcv24h();
    }

    public List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> loadSentiments24h() {
        return rssAdvisorCaller.predict24h();
    }

    public List<ChartMetricRecord> loadMetrics24h() {
        return chartMetricCaller.metric24h();
    }

    public List<ChartEntry> loadAllChartEntries() {
        return chartDataCollectionCaller.ohlcv();
    }

    public List<ChartMetricRecord> loadAllMetrics() {
        return chartMetricCaller.metricAll();
    }

    public List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> loadAllSentiments() {
        return rssAdvisorCaller.getAllSentiments();
    }

    public List<TradingEvaluationResult> loadAllMetricSentiments() {
        return chartAdvisorCaller.getAllSentiments();
    }

    public List<TradingEvaluationResult> loadMetricSentiments24h() {
        return chartAdvisorCaller.predict24h();
    }
}
