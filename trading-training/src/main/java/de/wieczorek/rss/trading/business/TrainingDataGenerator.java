package de.wieczorek.rss.trading.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.trading.business.data.Account;
import de.wieczorek.rss.trading.business.data.ActionVertex;
import de.wieczorek.rss.trading.business.data.ActionVertexType;
import de.wieczorek.rss.trading.business.data.StateEdge;
import de.wieczorek.rss.trading.business.data.StateEdgePart;

@ApplicationScoped
public class TrainingDataGenerator {
    private static final int STEPPING = 10;
    private static final int SEQ_LENGTH = 120;
    private static final int DEPTH = 20;

    public StateEdge generateTrainingData() {
	List<TradingEvaluationResult> currentSentiment = loadSentiments();

	List<ChartEntry> chartEntries = loadChartEntries();

	System.out.println("found " + chartEntries.size() + " entries. first at " + chartEntries.get(0).getDate()
		+ " and last at " + chartEntries.get(chartEntries.size() - 1).getDate()); // TODO Logging

	List<StateEdgePart> stateParts = buildStateParts(chartEntries, currentSentiment);

	System.out.println("starting iteration"); // TODO logging

	Account startAcc = new Account();
	startAcc.setBtc(0);
	startAcc.setEur(1000);
	startAcc.setEurEquivalent(1000);

	int endIndex = Math.min(SEQ_LENGTH + STEPPING * DEPTH, stateParts.size());

	StateEdge rootState = buildState(stateParts, chartEntries, 0, SEQ_LENGTH, ActionVertexType.BUY, startAcc,
		endIndex);
	rootState.setId(0);

	Deque<StateEdge> statesToWorkOn = new LinkedList<>();
	statesToWorkOn.add(rootState);

	long idCounter = 1;
	while (!statesToWorkOn.isEmpty()) {
	    StateEdge currentState = statesToWorkOn.removeLast();

	    ActionVertex buy = new ActionVertex();
	    buy.setType(ActionVertexType.BUY);
	    buy.setTargetState(buildState(stateParts, chartEntries, currentState.getPartsStartIndex() + STEPPING,
		    currentState.getPartsEndIndex() + STEPPING, buy.getType(), currentState.getAccount(), endIndex));

	    ActionVertex sell = new ActionVertex();
	    sell.setType(ActionVertexType.SELL);
	    sell.setTargetState(buildState(stateParts, chartEntries, currentState.getPartsStartIndex() + STEPPING,
		    currentState.getPartsEndIndex() + STEPPING, sell.getType(), currentState.getAccount(), endIndex));

	    if (buy.getTargetState() != null) {
		statesToWorkOn.add(buy.getTargetState());
		currentState.getActions().add(buy);
		buy.getTargetState().setPrevious(currentState);
		buy.getTargetState().setId(idCounter++);
	    }

	    if (sell.getTargetState() != null) {
		statesToWorkOn.add(sell.getTargetState());
		currentState.getActions().add(sell);
		sell.getTargetState().setPrevious(currentState);
		sell.getTargetState().setId(idCounter++);
	    }
	}

	System.out.println("Finished building tree");

	return rootState;

    }

    private List<StateEdgePart> buildStateParts(List<ChartEntry> chartEntries,
	    List<TradingEvaluationResult> currentSentiment) {
	Map<LocalDateTime, TradingEvaluationResult> sentimentDateMappings = currentSentiment.stream().collect(
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

    private List<ChartEntry> loadChartEntries() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});
    }

    private List<TradingEvaluationResult> loadSentiments() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12020/sentiment/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<TradingEvaluationResult>>() {
		});
    }

    private StateEdge buildState(List<StateEdgePart> stateParts, List<ChartEntry> chartEntries, int partStartIndex,
	    int partEndIndex, ActionVertexType action, Account acc, int lastIndex) {
	StateEdge currentState = new StateEdge();

	if (partStartIndex >= lastIndex || partEndIndex > lastIndex) {
	    return null;
	}

	Account newAcc = new Account();
	if (action == ActionVertexType.BUY) {
	    if (acc.getBtc() > 0.0) {
		newAcc.setBtc(acc.getBtc());
		newAcc.setEur(0);
		newAcc.setEurEquivalent(newAcc.getBtc() * chartEntries.get(partEndIndex).getClose());
	    } else {
		newAcc.setBtc((acc.getEur() / chartEntries.get(partEndIndex).getClose()) * ((100 - 0.2) / 100));
		newAcc.setEur(0);
		newAcc.setEurEquivalent(newAcc.getBtc() * chartEntries.get(partEndIndex).getClose());
	    }

	} else {
	    if (acc.getBtc() > 0.0) {
		newAcc.setBtc(0);
		newAcc.setEur(acc.getBtc() * chartEntries.get(partEndIndex).getClose() * ((100 - 0.2) / 100));
		newAcc.setEurEquivalent(newAcc.getEur());
	    } else {
		newAcc.setBtc(0);
		newAcc.setEur(acc.getEur());
		newAcc.setEurEquivalent(newAcc.getEur());
	    }
	}

	currentState.setAccount(newAcc);
	currentState.setPartsStartIndex(partStartIndex);
	currentState.setPartsEndIndex(partEndIndex);
	currentState.setAllStateParts(stateParts);

	return currentState;
    }

}
