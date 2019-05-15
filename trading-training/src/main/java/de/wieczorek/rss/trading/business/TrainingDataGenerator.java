package de.wieczorek.rss.trading.business;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.trading.business.data.ActionVertex;
import de.wieczorek.rss.trading.business.data.StateEdge;
import de.wieczorek.rss.trading.common.BuySellHelper;
import de.wieczorek.rss.trading.common.DataLoader;
import de.wieczorek.rss.trading.common.StatePartBuilder;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdgePart;

@ApplicationScoped
public class TrainingDataGenerator {
    private static final int STEPPING = 10;
    private static final int SEQ_LENGTH = 1;
    private static final int DEPTH = 20;

    @Inject
    private DataLoader dataLoader;

    List<TradingEvaluationResult> currentSentiment;
    List<ChartEntry> chartEntries;

    List<ChartMetricRecord> chartMetrics;

    public void loadData() {
	currentSentiment = dataLoader.loadAllSentiments();
	chartEntries = dataLoader.loadAllChartEntries();
	chartMetrics = dataLoader.loadAllChartMetrics();

    }

    public StateEdge generateTrainingData(int offset) {

	System.out.println("found " + chartEntries.size() + " entries. first at " + chartEntries.get(0).getDate()
		+ " and last at " + chartEntries.get(chartEntries.size() - 1).getDate()); // TODO Logging

	List<StateEdgePart> stateParts = StatePartBuilder.buildStateParts(chartEntries, chartMetrics, currentSentiment);

	System.out.println("starting iteration"); // TODO logging

	Account startAcc = new Account();
	startAcc.setBtc(0);
	startAcc.setEur(1000);
	startAcc.setEurEquivalent(1000);

	int endIndex = Math.min(offset + SEQ_LENGTH + STEPPING * DEPTH, stateParts.size());

	StateEdge rootState = buildState(stateParts, chartEntries, offset, offset + SEQ_LENGTH, ActionVertexType.BUY,
		startAcc, endIndex);
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

    public int getMaxIndex() {
	return chartEntries.size();
    }

    private StateEdge buildState(List<StateEdgePart> stateParts, List<ChartEntry> chartEntries, int partStartIndex,
	    int partEndIndex, ActionVertexType action, Account acc, int lastIndex) {
	StateEdge currentState = new StateEdge();

	if (partStartIndex >= lastIndex || partEndIndex > lastIndex) {
	    return null;
	}

	Account newAcc = new Account();
	double currentPrice = chartEntries.get(partEndIndex).getClose();
	if (action == ActionVertexType.BUY) {
	    newAcc = BuySellHelper.processBuy(currentPrice, acc);
	} else {
	    newAcc = BuySellHelper.processSell(currentPrice, acc);
	}

	currentState.setAccount(newAcc);
	currentState.setPartsStartIndex(partStartIndex);
	currentState.setPartsEndIndex(partEndIndex);
	currentState.setAllStateParts(stateParts);

	return currentState;
    }

}
