package de.wieczorek.rss.trading.types;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;

import java.util.Map;


public class StateEdgePart {
    private de.wieczorek.rss.advisor.types.TradingEvaluationResult sentiment;
    private ChartEntry chartEntry;
    private TradingEvaluationResult metricsSentiment;
    private DeltaChartEntry deltaChartEntry;
    private Map<Metric, ChartMetricRecord> metrics;
    private ContextProvider contextProvider;

    public ContextProvider getContextProvider() {
        return contextProvider;
    }

    public void setContextProvider(ContextProvider context) {
        this.contextProvider = context;
    }

    public Map<Metric, ChartMetricRecord> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<Metric, ChartMetricRecord> metrics) {
        this.metrics = metrics;
    }

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

    public enum Metric {
        RSI("rsi"),
        MACD("macd"),
        AROON("aroon"),
        STOCHASTIC_D("stochasticD");

        private String name;

        Metric(String name) {
            this.name = name;
        }

        public static Metric getValueForName(String name) {
            for (Metric op : values()) {
                if (op.name.equals(name)) {
                    return op;
                }
            }
            throw new RuntimeException("invalid name " + name);
        }

    }
}