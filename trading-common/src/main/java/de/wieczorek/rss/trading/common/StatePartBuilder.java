package de.wieczorek.rss.trading.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.trading.types.StateEdgePart;

public final class StatePartBuilder {
    private StatePartBuilder() {

    }

    public static List<StateEdgePart> buildStateParts(List<ChartEntry> chartEntries,
	    List<TradingEvaluationResult> sentiments) {
	Map<LocalDateTime, TradingEvaluationResult> sentimentDateMappings = sentiments.stream().collect(
		Collectors.toMap(TradingEvaluationResult::getCurrentTime, Function.identity(), (v1, v2) -> v2));

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

	    part.setChartEntry(entry);
	    part.setSentiment(sentimentDateMappings.get(entry.getDate()));

	    stateParts.add(part);
	}
	return stateParts;
    }
}
