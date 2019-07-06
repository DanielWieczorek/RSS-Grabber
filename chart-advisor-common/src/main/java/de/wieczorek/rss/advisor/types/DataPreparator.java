package de.wieczorek.rss.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataPreparator {

    private List<ChartMetricRecord> sentiments;

    private List<ChartEntry> chartEntries;

    private int offsetMinutes = 15;

    private static Map<LocalDateTime, ChartEntry> chartEntryMappings;
    private static Map<LocalDateTime, ChartMetricRecord> sentimentDateMappings;

    public int getOffsetMinutes(){
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

    public NetInputItem getDataAtTime(LocalDateTime time){
    	return getData()
				.stream()
				.peek(item -> System.out.println(item.getDate()))
				.filter(item -> item.getDate().equals(time))
				.findFirst().orElse(null);
	}


    public List<NetInputItem> getData() {
	Map<LocalDateTime, List<ChartMetricRecord>> sentimentDateMappings = new HashMap<>();
	sentiments.forEach(metric ->
		sentimentDateMappings.merge(metric.getId().getDate(), Arrays.asList(metric),(newMetric, oldMetric) -> {
			List result = new ArrayList<>();
			result.addAll(newMetric);
			result.addAll(oldMetric);
			return result;
		})
		);


	Map<LocalDateTime, ChartEntry> chartEntryMappings = chartEntries.stream()
		.collect(Collectors.toMap(ChartEntry::getDate, Function.identity(), (v1, v2) -> v2));

	List<NetInputItem> result = new ArrayList<>();

	chartEntryMappings.keySet().forEach(date -> {
	    ChartEntry inputChartEntry = chartEntryMappings.get(date);
		List<ChartMetricRecord> inputSentiment = sentimentDateMappings.get(date);

		ChartEntry outputChartEntry = chartEntryMappings.get(date.plusMinutes(offsetMinutes));
	    if (inputChartEntry != null && inputSentiment != null && outputChartEntry != null) {
			inputSentiment.sort(Comparator.comparing(metricRecord -> metricRecord.getId().getIndicator()));

		Map<ChartEntry,List<ChartMetricRecord>> entries = new HashMap<>();
		LocalDateTime currentDate = date.minusHours(4);
		while (currentDate.isBefore(inputChartEntry.getDate())) {
		    entries.put(chartEntryMappings.get(currentDate),sentimentDateMappings.get(currentDate));
		    currentDate = currentDate.plusMinutes(1);
			System.out.println("found entry at "+currentDate+" ?"+( sentimentDateMappings.get(currentDate)!= null ? "yes" : "no"));
		}
		entries.put(inputChartEntry,sentimentDateMappings.get(currentDate));

		NetInputItem item = new NetInputItem();
		item.setInputChartMetrics(entries);
		item.setOutputDelta(outputChartEntry.getClose() - inputChartEntry.getClose());
		item.setDate(outputChartEntry.getDate());

			System.out.println("dates: "+date);


		if (entries.values().stream().filter(x -> x == null).count() == 0) {
			result.add(item);
		}
	    }
	});

	return result;
    }

}
