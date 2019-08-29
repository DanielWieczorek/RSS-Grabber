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

    private static Map<LocalDateTime, ChartEntry> chartEntryMappings;
    private static Map<LocalDateTime, ChartMetricRecord> sentimentDateMappings;

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
        LocalDateTime date = time.minusHours(24);

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


        ChartEntry inputChartEntry = chartEntryMappings.get(time.minusMinutes(1));
        List<ChartMetricRecord> inputSentiment = sentimentDateMappings.get(time.minusMinutes(1));

        return buildNetworkInputItem(time, sentimentDateMappings, chartEntryMappings, inputChartEntry, inputSentiment, true);

    }

    private NetInputItem buildNetworkInputItem(LocalDateTime time, Map<LocalDateTime, List<ChartMetricRecord>> sentimentDateMappings, Map<LocalDateTime, ChartEntry> chartEntryMappings, ChartEntry inputChartEntry, List<ChartMetricRecord> inputSentiment, boolean sampleSentiments) {
        if (inputChartEntry != null && inputSentiment != null) {
            inputSentiment.sort(Comparator.comparing(metricRecord -> metricRecord.getId().getIndicator()));


            Map<ChartEntry, List<ChartMetricRecord>> entries = new HashMap<>();
            List<ChartEntry> chartEntries = new ArrayList<>();
            LocalDateTime currentDate = time.minusHours(24).plusMinutes(1);

            List<ChartMetricRecord> lastSentiment = null;
            ChartEntry lastEntry = null;
            while (currentDate.isBefore(inputChartEntry.getDate())) {


                List<ChartMetricRecord> currentSentiment = sentimentDateMappings.get(currentDate);

                if (currentSentiment != null) {
                    currentSentiment.sort(Comparator.comparing(metricRecord -> metricRecord.getId().getIndicator()));
                } else if (lastSentiment != null && sampleSentiments) {
                    currentSentiment = lastSentiment;
                } else if (sampleSentiments) {
                    currentSentiment = new ArrayList<>();
                    currentSentiment.add(new ChartMetricRecord());
                    currentSentiment.add(new ChartMetricRecord());
                    currentSentiment.add(new ChartMetricRecord());
                    currentSentiment.add(new ChartMetricRecord());

                }
                if (currentSentiment == null) {
                    return null;
                }

                ChartEntry entry = chartEntryMappings.get(currentDate);
                if (entry != null) {
                    lastEntry = entry;
                } else {
                    entry = lastEntry;
                }

                entries.put(entry, currentSentiment);
                chartEntries.add(entry);

                currentDate = currentDate.plusMinutes(1);
                lastSentiment = currentSentiment;
            }
            entries.put(inputChartEntry, sentimentDateMappings.get(currentDate));
            chartEntries.add(inputChartEntry);

            NetInputItem result = new NetInputItem();
            result.setInputChartMetrics(entries);
            result.setChartEntries(chartEntries);
            result.setDate(time);
            if (entries.keySet().stream().filter(x -> x == null).count() > 0) {
                return null;
            }

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


        Map<LocalDateTime, ChartEntry> chartEntryMappings = chartEntries.stream()
                .collect(Collectors.toMap(ChartEntry::getDate, Function.identity(), (v1, v2) -> v2));

        List<NetInputItem> result = new ArrayList<>();
        LocalDateTime lastDate = chartEntryMappings.keySet().stream().reduce((x, y) -> x.isAfter(y) ? x : y).get();

        chartEntryMappings.keySet().stream().filter(entry -> entry.isBefore(lastDate.minusHours(50))).forEach(date -> {
            ChartEntry inputChartEntry = chartEntryMappings.get(date);
            List<ChartMetricRecord> inputSentiment = sentimentDateMappings.get(date);

            ChartEntry outputChartEntry = chartEntryMappings.get(date.plusMinutes(offsetMinutes));
            NetInputItem item = buildNetworkInputItem(date, sentimentDateMappings, chartEntryMappings, inputChartEntry, inputSentiment, false);

            if (item != null && inputChartEntry != null && inputSentiment != null && outputChartEntry != null) {
                item.setOutputDelta(outputChartEntry.getClose() - inputChartEntry.getClose());
                result.add(item);
            }
        });

        return result;
    }

}
