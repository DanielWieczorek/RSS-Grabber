package de.wieczorek.rss.trading.common;


import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;
import de.wieczorek.rss.trading.types.StateEdgePart;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

public class DataGenerator {
    private static final int STEPPING = 5;
    private static final int SEQ_LENGTH = 5;
    private static final int DEPTH = 1440;

    @Inject
    private DataLoader dataLoader;

    @Inject
    private MetricNormalizer normalizer;

    private List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> currentSentiment;

    private List<TradingEvaluationResult> currentMetricSentiment;


    private List<ChartEntry> chartEntries;

    private List<ChartMetricRecord> chartMetrics;

    List<StateEdgePart> stateParts;


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

        int endIndex = Math.min(offset + SEQ_LENGTH + STEPPING * DEPTH, stateParts.size()-1);

        StateEdge rootState = buildState(stateParts, chartEntries, offset, offset + SEQ_LENGTH, ActionVertexType.BUY,
                startAcc, endIndex);
        rootState.setId(0);

        return rootState;
    }

    public StateEdge buildNextState(StateEdge currentState, ActionVertexType action) {
        int endIndex = Math.min(currentState.getPartsEndIndex() + SEQ_LENGTH + STEPPING * DEPTH, stateParts.size());

        return buildState(stateParts, chartEntries, currentState.getPartsStartIndex() + STEPPING,
                currentState.getPartsEndIndex() + STEPPING, action, currentState.getAccount(), endIndex);
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
