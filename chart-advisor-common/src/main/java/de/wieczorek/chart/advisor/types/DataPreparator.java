package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataPreparator {
    private static final Logger logger = LoggerFactory.getLogger(DataPreparator.class);
    private List<ChartMetricRecord> sentiments;
    private List<ChartEntry> chartEntries;
    private int offsetMinutes = 60;

    public int getOffsetMinutes() {
        return offsetMinutes;
    }

    public DataPreparator withMetrics(List<ChartMetricRecord> metrics) {
        this.sentiments = metrics;
        return this;
    }

    public DataPreparator withChartData(List<ChartEntry> chartEntries) {
        this.chartEntries = chartEntries;
        return this;
    }

    public NetInputItem getDataAtTime(LocalDateTime time) {
        LocalDateTime date = time.minusHours(24).minusMinutes(1);

        Map<LocalDateTime, List<ChartMetricRecord>> sentimentDateMappings = new HashMap<>();
        sentiments.stream().filter(x -> x.getId().getDate().isAfter(date) ||
                x.getId().getDate().isEqual(date)).forEach(metric ->
                sentimentDateMappings.merge(metric.getId().getDate(), Arrays.asList(metric), (newMetric, oldMetric) -> {
                    List result = new ArrayList<>();
                    result.addAll(newMetric);
                    result.addAll(oldMetric);
                    return result;
                })
        );

        Map<LocalDateTime, ChartEntry> chartEntryMappings = chartEntries.stream()
                .filter(x -> x.getDate().isAfter(date) ||
                        x.getDate().isEqual(date))
                .collect(Collectors.toMap(ChartEntry::getDate, Function.identity(), (v1, v2) -> v2));

        interpolate(sentimentDateMappings, chartEntryMappings);

        List<LocalDateTime> dates = chartEntryMappings.keySet().stream().sorted().collect(Collectors.toList());

        Map<LocalDateTime, Integer> dateIndexMapping = new HashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            dateIndexMapping.put(dates.get(i), i);
        }

        return buildNetworkInputItem(time, sentimentDateMappings, chartEntryMappings, dateIndexMapping, dates);

    }

    private NetInputItem buildNetworkInputItem(LocalDateTime time, Map<LocalDateTime, List<ChartMetricRecord>> sentimentDateMappings, Map<LocalDateTime, ChartEntry> chartEntryMappings, Map<LocalDateTime, Integer> dateIndexMapping, List<LocalDateTime> times) {
        LocalDateTime startDate = time.minusHours(24).plusMinutes(1);

        Integer startIndex = dateIndexMapping.get(startDate);
        Integer endIndex = dateIndexMapping.get(time);

        if (startIndex != null && endIndex != null) {
            NetInputItem result = new NetInputItem();
            result.setInputChartMetrics(sentimentDateMappings);
            result.setChartEntries(chartEntryMappings);
            result.setDates(times);
            result.setDate(time);
            result.setStartIndex(startIndex);
            result.setEndIndex(endIndex);

            return result;
        }

        return null;
    }


    public List<NetInputItem> getData() {
        Map<LocalDateTime, List<ChartMetricRecord>> sentimentDateMappings = new HashMap<>();


        sentiments.forEach(metric ->
                sentimentDateMappings.merge(metric.getId().getDate(), Arrays.asList(metric), (newMetric, oldMetric) -> {
                    List result = new ArrayList<>();
                    result.addAll(newMetric);
                    result.addAll(oldMetric);
                    return result;
                })
        );

        Map<LocalDateTime, ChartEntry> chartEntryMappings = new HashMap<>();
        chartEntries.forEach(entry -> chartEntryMappings.put(entry.getDate(), entry));

        List<LocalDateTime> dates = chartEntryMappings.keySet().stream().sorted().collect(Collectors.toList());

        List<NetInputItem> result = new ArrayList<>();
        LocalDateTime lastDate = chartEntryMappings.keySet().stream().reduce((x, y) -> x.isAfter(y) ? x : y).get();

        Map<LocalDateTime, Integer> dateIndexMapping = new HashMap<>();
        for (int i = 0; i < dates.size(); i++) {
            dateIndexMapping.put(dates.get(i), i);
        }

        chartEntryMappings.keySet().stream()
                //.filter(entry -> entry.isBefore(lastDate.minusHours(50)))
                .forEach(date -> {
                    ChartEntry inputChartEntry = chartEntryMappings.get(date);
                    List<ChartMetricRecord> inputSentiment = sentimentDateMappings.get(date);


                    ChartEntry outputChartEntry = chartEntryMappings.get(date.plusMinutes(offsetMinutes));
                    NetInputItem item = buildNetworkInputItem(date, sentimentDateMappings, chartEntryMappings, dateIndexMapping, dates);


                    if (item != null && inputChartEntry != null && inputSentiment != null && outputChartEntry != null) {
                        item.setOutputDelta(outputChartEntry.getClose() - inputChartEntry.getClose());
                        result.add(item);
                    }
                });

        return result;
    }

    private void interpolate(Map<LocalDateTime, List<ChartMetricRecord>> sentimentDateMappings, Map<LocalDateTime, ChartEntry> chartEntryMappings) {
        LocalDateTime currentDate = chartEntries.stream().map(ChartEntry::getDate).reduce((a, b) -> a.isBefore(b) ? a : b).get();
        LocalDateTime maxDate = chartEntries.stream().map(ChartEntry::getDate).reduce((a, b) -> a.isAfter(b) ? a : b).get();
        List<ChartMetricRecord> lastSentiment = null;
        ChartEntry lastEntry = null;
        while (currentDate.isBefore(maxDate)) {
            List<ChartMetricRecord> currentSentiment = sentimentDateMappings.get(currentDate);

            if (currentSentiment != null) {
                currentSentiment.sort(Comparator.comparing(metricRecord -> metricRecord.getId().getIndicator()));
            } else if (lastSentiment != null) {
                currentSentiment = lastSentiment;
            } else {
                currentSentiment = new ArrayList<>();
                currentSentiment.add(new ChartMetricRecord());
                currentSentiment.add(new ChartMetricRecord());
                currentSentiment.add(new ChartMetricRecord());
                currentSentiment.add(new ChartMetricRecord());
            }

            ChartEntry entry = chartEntryMappings.get(currentDate);
            if (entry != null) {
                lastEntry = entry;
            } else {
                entry = lastEntry;
            }

            sentimentDateMappings.put(currentDate, currentSentiment);
            chartEntryMappings.put(currentDate, entry);

            currentDate = currentDate.plusMinutes(1);
            lastSentiment = currentSentiment;
        }
    }

}
