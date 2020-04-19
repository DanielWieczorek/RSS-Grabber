package de.wieczorek.rss.trading.common.io;


import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.trading.common.trading.BuySellHelper;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.List;
import java.util.function.Supplier;

public class DataGenerator {
    private static final int STEPPING = 5;
    private static final int DEPTH = 1440;
    private List<StateEdgePart> stateParts;
    private List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> currentSentiment;
    private List<TradingEvaluationResult> currentMetricSentiment;
    private List<ChartEntry> chartEntries;


    public DataGenerator(Supplier<List<de.wieczorek.rss.advisor.types.TradingEvaluationResult>> sentimentSupplier,
                         Supplier<List<ChartEntry>> chartEntrySupplier,
                         Supplier<List<TradingEvaluationResult>> metricSupplier) {

        currentSentiment = sentimentSupplier.get();
        chartEntries = chartEntrySupplier.get();
        currentMetricSentiment = metricSupplier.get();
        stateParts = StatePartBuilder.buildStateParts(chartEntries, currentMetricSentiment, currentSentiment);
    }

    public StateEdge buildNewStartState(int offset) {
        Account startAcc = new Account();
        startAcc.setBtc(0);
        startAcc.setEur(1000);
        startAcc.setEurEquivalent(1000);

        int endIndex = getEndIndex(offset);

        StateEdge rootState = buildState(offset, offset + 5, ActionVertexType.NOTHING,
                startAcc, endIndex);
        rootState.setId(0);

        return rootState;
    }

    private int getEndIndex(int offset) {
        return Math.min(offset + STEPPING * DEPTH, stateParts.size() - 1);
    }

    public StateEdge buildNextState(StateEdge currentState, ActionVertexType action) {
        int endIndex = getEndIndex(currentState.getPartsEndIndex());

        return buildState(currentState.getPartsStartIndex() + STEPPING,
                currentState.getPartsEndIndex() + STEPPING, action, currentState.getAccount(), endIndex);
    }


    public int getMaxIndex() {
        return chartEntries.size();
    }

    private StateEdge buildState(int partStartIndex,
                                 int partEndIndex, ActionVertexType action, Account acc, int lastIndex) {
        StateEdge currentState = new StateEdge();

        if (partStartIndex >= lastIndex || partEndIndex > lastIndex) {
            return null;
        }

        Account newAcc = null;
        double currentPrice = chartEntries.get(partEndIndex).getClose();
        if (action == ActionVertexType.BUY) {
            newAcc = BuySellHelper.processBuy(currentPrice, acc);
        } else if (action == ActionVertexType.SELL) {
            newAcc = BuySellHelper.processSell(currentPrice, acc);
        } else {
            newAcc = BuySellHelper.processNoop(currentPrice, acc);
        }

        currentState.setAccount(newAcc);
        currentState.setPartsStartIndex(partStartIndex);
        currentState.setPartsEndIndex(partEndIndex);
        currentState.setAllStateParts(stateParts);

        return currentState;
    }

    public StateEdge BuildLastStateEdge(Account acc) {
        StateEdge currentState = new StateEdge();

        currentState.setAccount(acc);
        currentState.setPartsStartIndex(stateParts.size() - 1);
        currentState.setPartsEndIndex(stateParts.size() - 1);
        currentState.setAllStateParts(stateParts);
        return currentState;
    }

}
