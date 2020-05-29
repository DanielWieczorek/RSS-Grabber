package de.wieczorek.rss.trading.common.io;


import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.trading.common.trading.BuySellHelper;
import de.wieczorek.rss.trading.types.*;

import java.util.List;
import java.util.function.Supplier;

public class DataGenerator {
    private static final int STEPPING = 1;
    private static final int DEPTH = 1440;
    private static final int WIDTH = 60;
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
        StateEdgeChainMetaInfo metaInfo = new StateEdgeChainMetaInfo();
        metaInfo.setOffset(offset);
        metaInfo.setDepth(DEPTH);
        metaInfo.setStepping(STEPPING);
        metaInfo.setWidth(WIDTH);

        StateEdge rootState = buildNewStartState(metaInfo);
        rootState.setId(0);

        return rootState;
    }

    public StateEdge buildNewStartState(StateEdgeChainMetaInfo metaInfo) {
        Account startAcc = new Account();
        startAcc.setBtc(0);
        startAcc.setEur(1000);
        startAcc.setEurEquivalent(1000);

        int endIndex = getEndIndex(metaInfo);

        StateEdge rootState = buildState(metaInfo.getOffset(), metaInfo.getOffset() + metaInfo.getWidth(), ActionVertexType.NOTHING,
                startAcc, endIndex, metaInfo);
        rootState.setId(0);

        return rootState;
    }

    private int getEndIndex(StateEdgeChainMetaInfo metaInfo) {
        return Math.min(metaInfo.getOffset() + metaInfo.getStepping() * metaInfo.getDepth(), stateParts.size() - 1);
    }

    public StateEdge buildNextState(StateEdge currentState, ActionVertexType action) {
        int endIndex = getEndIndex(currentState.getMetaInfo());

        return buildState(currentState.getPartsStartIndex() + STEPPING,
                currentState.getPartsEndIndex() + STEPPING, action, currentState.getAccount(), endIndex, currentState.getMetaInfo());
    }


    public int getMaxIndex() {
        return chartEntries.size();
    }

    private StateEdge buildState(int partStartIndex,
                                 int partEndIndex, ActionVertexType action, Account acc, int lastIndex,
                                 StateEdgeChainMetaInfo metaInfo) {
        StateEdge currentState = new StateEdge();

        if (partStartIndex >= lastIndex || partEndIndex > lastIndex) {
            return null;
        }

        Account newAcc;
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
        currentState.setMetaInfo(metaInfo);

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
