package de.wieczorek.rss.trading.ui;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.factory.Nd4j;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.nn.PolicyDao;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.ui.ControllerBase;
import de.wieczorek.rss.trading.business.data.InputDataSnapshot;
import de.wieczorek.rss.trading.business.data.Trade;
import de.wieczorek.rss.trading.common.BuySellHelper;
import de.wieczorek.rss.trading.common.DataLoader;
import de.wieczorek.rss.trading.common.NetworkInputBuilder;
import de.wieczorek.rss.trading.common.StatePartBuilder;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdgePart;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @Inject
    private PolicyDao dao;

    @Inject
    private DataLoader dataLoader;

    public List<Trade> simulate() {
	DQNPolicy<?> policy = dao.readPolicy();
	List<ChartEntry> chartEntries = dataLoader.loadChartEntries24h();
	List<TradingEvaluationResult> sentiments = dataLoader.loadSentiments24h();

	Account account = buildAccount();
	List<InputDataSnapshot> snapshots = buildInputData(chartEntries, sentiments);
	List<Trade> trades = new ArrayList<>();

	for (int i = 0; i < snapshots.size(); i += 10) {
	    InputDataSnapshot snapshot = snapshots.get(i);
	    Account newAccount = performTrade(policy, snapshot, account);
	    addTrade(trades, account, newAccount, snapshot);
	    account = newAccount;
	}
	return trades;
    }

    private void addTrade(List<Trade> trades, Account account, Account newAccount, InputDataSnapshot snapshot) {

	Trade newTrade = new Trade();
	newTrade.setDate(snapshot.getAllStateParts().get(snapshot.getPartsEndIndex()).getChartEntry().getDate());
	newTrade.setBefore(account);
	newTrade.setAfter(newAccount);
	newTrade.setCurrentRate(snapshot.getCurrentRate().getClose());

	if (account.getBtc() > newAccount.getBtc()) { // Sell
	    newTrade.setAction(ActionVertexType.SELL);
	    trades.add(newTrade);
	} else if (account.getBtc() < newAccount.getBtc()) { // Buy
	    newTrade.setAction(ActionVertexType.BUY);
	    trades.add(newTrade);
	}
    }

    private Account performTrade(DQNPolicy<?> policy, InputDataSnapshot snapshot, Account account) {
	double[] inputData = NetworkInputBuilder.buildInputArray(snapshot, account);
	Integer result = policy.nextAction(Nd4j.create(inputData));
	double currentPrice = getCurrentPrice(snapshot);
	if (result == 0) {
	    Account newAcc = BuySellHelper.processBuy(currentPrice, account);
	    System.out.println("buy/hold @ " + currentPrice + ". got " + newAcc.getBtc() + " BTC, " + newAcc.getEur()
		    + " EUR, " + newAcc.getEurEquivalent() + " EUREQ");
	    return newAcc;
	} else {
	    Account newAcc = BuySellHelper.processSell(currentPrice, account);
	    System.out.println("sell/hold @ " + currentPrice + ". got " + newAcc.getBtc() + " BTC, " + newAcc.getEur()
		    + " EUR, " + newAcc.getEurEquivalent() + " EUREQ");
	    return newAcc;
	}
    }

    private double getCurrentPrice(InputDataSnapshot snapshot) {
	return snapshot.getCurrentRate().getClose();
    }

    private Account buildAccount() {
	Account result = new Account();
	result.setBtc(0);
	result.setEur(1000);
	result.setEurEquivalent(1000);
	return result;
    }

    private List<InputDataSnapshot> buildInputData(List<ChartEntry> chartEntries,
	    List<TradingEvaluationResult> sentiments) {
	List<StateEdgePart> parts = StatePartBuilder.buildStateParts(chartEntries, sentiments);

	List<InputDataSnapshot> result = new ArrayList<>();
	for (int i = 0; i < parts.size() - 120; i++) {

	    result.add(buildInputDataSnapshot(i, i + 120, parts, chartEntries.get(i + 120)));
	}

	return result;
    }

    private InputDataSnapshot buildInputDataSnapshot(int startIndex, int endIndex, List<StateEdgePart> parts,
	    ChartEntry chartEntry) {
	InputDataSnapshot result = new InputDataSnapshot();
	result.setAllStateParts(parts);
	result.setPartsEndIndex(endIndex);
	result.setPartsStartIndex(startIndex);
	result.setCurrentRate(chartEntry);

	return result;
    }

}
