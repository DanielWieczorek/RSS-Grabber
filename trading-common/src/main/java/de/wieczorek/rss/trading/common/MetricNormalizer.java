package de.wieczorek.rss.trading.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

@ApplicationScoped
public class MetricNormalizer {

    @Inject
    private DataLoader dataLoader;

    private Map<String, Map<Integer, Double>> minValues = new HashMap<>();

    private Map<String, Map<Integer, Double>> maxValues = new HashMap<>();

    @PostConstruct
    private void init() {
        List<ChartMetricRecord> chartMetrics = dataLoader.loadAllMetrics();

        for (ChartMetricRecord record : chartMetrics) {
            Map<Integer, Double> found = minValues.computeIfAbsent(record.getId().getIndicator(),
                    (k) -> new HashMap<>());
            found.merge(1, record.getValue1min(), (oldValue, newValue) -> Math.min(oldValue, newValue));
            found.merge(5, record.getValue5min(), (oldValue, newValue) -> Math.min(oldValue, newValue));
            found.merge(15, record.getValue15min(), (oldValue, newValue) -> Math.min(oldValue, newValue));
            found.merge(30, record.getValue30min(), (oldValue, newValue) -> Math.min(oldValue, newValue));
            found.merge(60, record.getValue60min(), (oldValue, newValue) -> Math.min(oldValue, newValue));
        }

        for (ChartMetricRecord record : chartMetrics) {
            Map<Integer, Double> found = maxValues.computeIfAbsent(record.getId().getIndicator(),
                    (k) -> new HashMap<>());
            found.merge(1, record.getValue1min(), (oldValue, newValue) -> Math.max(oldValue, newValue));
            found.merge(5, record.getValue5min(), (oldValue, newValue) -> Math.max(oldValue, newValue));
            found.merge(15, record.getValue15min(), (oldValue, newValue) -> Math.max(oldValue, newValue));
            found.merge(30, record.getValue30min(), (oldValue, newValue) -> Math.max(oldValue, newValue));
            found.merge(60, record.getValue60min(), (oldValue, newValue) -> Math.max(oldValue, newValue));
        }
    }

    public ChartMetricRecord normalize(ChartMetricRecord inputRecord) {
        ChartMetricRecord outputRecord = new ChartMetricRecord();

        outputRecord.setId(inputRecord.getId());
        outputRecord.setValue1min(normalizeValue(inputRecord.getId().getIndicator(), 1, inputRecord.getValue1min()));
        outputRecord.setValue5min(normalizeValue(inputRecord.getId().getIndicator(), 5, inputRecord.getValue5min()));
        outputRecord.setValue15min(normalizeValue(inputRecord.getId().getIndicator(), 15, inputRecord.getValue15min()));

        outputRecord.setValue30min(normalizeValue(inputRecord.getId().getIndicator(), 30, inputRecord.getValue30min()));
        outputRecord.setValue60min(normalizeValue(inputRecord.getId().getIndicator(), 60, inputRecord.getValue60min()));

        return outputRecord;
    }

    private double normalizeValue(String indicator, int i, double value) {
        double minValue = minValues.get(indicator).get(i);
        double maxValue = maxValues.get(indicator).get(i);

        return (value - minValue) / (maxValue - minValue);
    }

}
