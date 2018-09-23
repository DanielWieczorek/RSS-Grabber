package de.wieczorek.rss.insight.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.wieczorek.rss.insight.persistence.ChartEntry;
import de.wieczorek.rss.insight.persistence.SentimentAtTime;

public class DataPreparator {

    private List<SentimentAtTime> sentiments;

    private List<ChartEntry> chartEntries;

    private int offsetMinutes = 60;

    public DataPreparator withSentiments(List<SentimentAtTime> sentiments) {
	this.sentiments = sentiments;
	return this;
    }

    public DataPreparator withChartData(List<ChartEntry> chartEntries) {
	this.chartEntries = chartEntries;
	return this;
    }

    public List<TrainingDataItem> getData() {
	Map<LocalDateTime, SentimentAtTime> sentimentDateMappings = sentiments.stream()
		.collect(Collectors.toMap(SentimentAtTime::getSentimentTime, Function.identity(), (v1, v2) -> v2));

	Map<LocalDateTime, ChartEntry> chartEntryMappings = chartEntries.stream()
		.collect(Collectors.toMap(ChartEntry::getDate, Function.identity(), (v1, v2) -> v2));

	List<TrainingDataItem> result = new ArrayList<>();

	chartEntryMappings.keySet().forEach(date -> {
	    ChartEntry inputChartEntry = chartEntryMappings.get(date);
	    SentimentAtTime inputSentiment = sentimentDateMappings.get(date);

	    ChartEntry outputChartEntry = chartEntryMappings.get(date.plusMinutes(offsetMinutes));
	    if (inputChartEntry != null && inputSentiment != null && outputChartEntry != null) {

		List<ChartEntry> entries = new ArrayList<>();
		LocalDateTime currentDate = date.minusHours(24);
		while (currentDate.isBefore(inputChartEntry.getDate())) {
		    entries.add(chartEntryMappings.get(currentDate));
		    currentDate = currentDate.plusMinutes(1);
		}
		entries.add(inputChartEntry);

		List<DeltaChartEntry> inputChartEntries = entries.stream().map(current -> {
		    if (current != null) {
			LocalDateTime dateToCheck = current.getDate().minusMinutes(1);
			ChartEntry previous = chartEntryMappings.get(dateToCheck);
			while (previous == null && dateToCheck.plusHours(24).isAfter(current.getDate())) {
			    dateToCheck = dateToCheck.minusMinutes(1);
			    previous = chartEntryMappings.get(dateToCheck);
			}
			if (previous != null) {
			    DeltaChartEntry delta = new DeltaChartEntry();
			    delta.setDate(current.getDate());
			    delta.setHigh(current.getHigh() - previous.getHigh());
			    delta.setLow(current.getLow() - previous.getLow());
			    delta.setOpen(current.getOpen() - previous.getOpen());
			    delta.setClose(current.getClose() - previous.getClose());
			    delta.setTransactions(current.getTransactions() - previous.getTransactions());
			    delta.setVolume(current.getVolume() - previous.getVolume());
			    delta.setVolumeWeightedAverage(
				    current.getVolumeWeightedAverage() - previous.getVolumeWeightedAverage());

			    return delta;
			}
		    }
		    return null;
		}).collect(Collectors.toList());

		int outputSentiment = outputChartEntry.getClose() - inputChartEntry.getClose() > 0.0 ? 1 : -1;

		TrainingDataItem item = new TrainingDataItem();
		item.setInputChartEntry(inputChartEntries);
		item.setInputSentiment(inputSentiment);
		item.setOutputSentiment(outputSentiment);

		result.add(item);
	    }
	});
	return result;
    }

    public TrainingDataItem getDataForSentiment(SentimentAtTime sentiment) {

	Map<LocalDateTime, ChartEntry> chartEntryMappings = chartEntries.stream()
		.peek(e -> System.out.println(e.getDate()))
		.collect(Collectors.toMap(ChartEntry::getDate, Function.identity(), (v1, v2) -> v2));

	LocalDateTime startTime = sentiment.getSentimentTime().minusHours(24);
	LocalDateTime currentDate = startTime;

	ChartEntry inputChartEntry = chartEntryMappings.get(currentDate);
	while (inputChartEntry == null) {
	    inputChartEntry = chartEntryMappings.get(currentDate);
	    currentDate = currentDate.plusMinutes(1);
	}

	if (inputChartEntry != null) {
	    List<ChartEntry> entries = new ArrayList<>();
	    while (currentDate.isBefore(sentiment.getSentimentTime())
		    || currentDate.isEqual(sentiment.getSentimentTime())) {
		inputChartEntry = chartEntryMappings.get(currentDate);
		entries.add(chartEntryMappings.get(currentDate));
		currentDate = currentDate.plusMinutes(1);
	    }

	    List<DeltaChartEntry> inputChartEntries = entries.stream().map(current -> {
		if (current != null) {
		    LocalDateTime dateToCheck = current.getDate().minusMinutes(1);
		    ChartEntry previous = chartEntryMappings.get(dateToCheck);
		    while (previous == null && dateToCheck.plusHours(24).isAfter(current.getDate())) {
			dateToCheck = dateToCheck.minusMinutes(1);
			previous = chartEntryMappings.get(dateToCheck);
		    }
		    if (previous != null) {
			DeltaChartEntry delta = new DeltaChartEntry();
			delta.setDate(current.getDate());
			delta.setHigh(current.getHigh() - previous.getHigh());
			delta.setLow(current.getLow() - previous.getLow());
			delta.setOpen(current.getOpen() - previous.getOpen());
			delta.setClose(current.getClose() - previous.getClose());
			delta.setTransactions(current.getTransactions() - previous.getTransactions());
			delta.setVolume(current.getVolume() - previous.getVolume());
			delta.setVolumeWeightedAverage(
				current.getVolumeWeightedAverage() - previous.getVolumeWeightedAverage());

			return delta;
		    }
		}
		return null;
	    }).collect(Collectors.toList());

	    TrainingDataItem item = new TrainingDataItem();
	    item.setInputChartEntry(inputChartEntries);
	    item.setInputSentiment(sentiment);
	    return item;
	}
	return null;
    }

}
