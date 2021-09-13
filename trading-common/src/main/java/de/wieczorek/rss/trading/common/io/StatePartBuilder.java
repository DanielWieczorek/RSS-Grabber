package de.wieczorek.rss.trading.common.io;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StatePartBuilder {
    private StatePartBuilder() {

    }

    public static List<StateEdgePart> buildStateParts(List<ChartEntry> chartEntries,
                                                      List<TradingEvaluationResult> chartMetricSentiments,
                                                      List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> sentiments,
                                                      List<ChartMetricRecord> chartMetrics) {
        Map<LocalDateTime, de.wieczorek.rss.advisor.types.TradingEvaluationResult> sentimentDateMappings = sentiments.stream()
                .collect(Collectors.toMap(de.wieczorek.rss.advisor.types.TradingEvaluationResult::getCurrentTime, Function.identity(), (v1, v2) -> v2));

        Map<LocalDateTime, TradingEvaluationResult> chartMetricSentimentMappings = chartMetricSentiments.stream()
                .collect(Collectors.toMap(TradingEvaluationResult::getCurrentTime, Function.identity(), (v1, v2) -> v2));

        Map<LocalDateTime, List<ChartMetricRecord>> chartMetricMappings = chartMetrics.stream()
                .collect(Collectors.groupingBy(metric -> metric.getId().getDate()));

        List<StateEdgePart> stateParts = new ArrayList<>();

        for (int i = 1; i < chartEntries.size(); i++) {
            StateEdgePart part = new StateEdgePart();
            ChartEntry previousEntry = chartEntries.get(i - 1);
            ChartEntry currentEntry = chartEntries.get(i);

            DeltaChartEntry entry = new DeltaChartEntry();
            entry.setDate(currentEntry.getDate());
            entry.setHigh(currentEntry.getHigh() - previousEntry.getHigh());
            entry.setLow(currentEntry.getLow() - previousEntry.getLow());
            entry.setOpen(currentEntry.getOpen() - previousEntry.getOpen());
            entry.setClose(currentEntry.getClose() - previousEntry.getClose());
            entry.setTransactions(currentEntry.getTransactions() - previousEntry.getTransactions());
            entry.setVolume(currentEntry.getVolume() - previousEntry.getVolume());
            entry.setVolumeWeightedAverage(
                    currentEntry.getVolumeWeightedAverage() - previousEntry.getVolumeWeightedAverage());

            part.setDeltaChartEntry(entry);
            part.setChartEntry(currentEntry);
            part.setSentiment(sentimentDateMappings.get(currentEntry.getDate()));
            part.setMetricsSentiment(chartMetricSentimentMappings.get(currentEntry.getDate()));
            part.setMetrics(chartMetricMappings.getOrDefault(currentEntry.getDate(), Collections.emptyList())
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(x -> StateEdgePart.Metric.getValueForName(x.getId().getIndicator()), Function.identity())));

            stateParts.add(part);
        }
        return stateParts;
    }
}
