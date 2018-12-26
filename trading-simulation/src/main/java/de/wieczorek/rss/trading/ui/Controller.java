package de.wieczorek.rss.trading.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.factory.Nd4j;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.nn.PolicyDao;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.ui.ControllerBase;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @Inject
    private PolicyDao dao;

    public void simulate() {
	DQNPolicy<?> policy = dao.readPolicy();
	List<ChartEntry> chartEntries = loadChartEntries();
	List<TradingEvaluationResult> sentiments = loadSentiments();

	Account account = buildAccount();
	List<InputDataSnapshot> snapshots = buildInputData(chartEntries, sentiments);
	for (int i = 0; i < snapshots.size(); i += 10) {
	    account = performTrade(policy, snapshots.get(i), account);
	}

    }

    private Account performTrade(DQNPolicy<?> policy, InputDataSnapshot snapshot, Account account) {
	double[] inputData = buildInputArray(snapshot, account);
	Integer result = policy.nextAction(Nd4j.create(inputData));
	if (result == 0) {
	    return processBuy(getCurrentPrice(snapshot), account);
	} else {
	    return processSell(getCurrentPrice(snapshot), account);
	}
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

    private Account processBuy(double currentPrice, Account acc) {
	Account newAcc = new Account();
	if (acc.getBtc() > 0.0) {
	    newAcc.setBtc(acc.getBtc());
	    newAcc.setEur(0);
	    newAcc.setEurEquivalent(newAcc.getBtc() * currentPrice);
	    System.out.println("hold @ " + currentPrice + ". got " + newAcc.btc + " BTC, " + newAcc.eur + " EUR, "
		    + newAcc.eurEquivalent + " EUREQ");
	} else {
	    newAcc.setBtc((acc.getEur() / currentPrice) * ((100 - 0.2) / 100));
	    newAcc.setEur(0);
	    newAcc.setEurEquivalent(newAcc.getBtc() * currentPrice);
	    System.out.println("buying @ " + currentPrice + ". got " + newAcc.btc + " BTC, " + newAcc.eur + " EUR, "
		    + newAcc.eurEquivalent + " EUREQ");
	}
	return newAcc;
    }

    private Account processSell(double currentPrice, Account acc) {
	Account newAcc = new Account();
	if (acc.getBtc() > 0.0) {
	    newAcc.setBtc(0);
	    newAcc.setEur(acc.getBtc() * currentPrice * ((100 - 0.2) / 100));
	    newAcc.setEurEquivalent(newAcc.getEur());
	    System.out.println("selling @ " + currentPrice + ". got " + newAcc.btc + " BTC, " + newAcc.eur + " EUR, "
		    + newAcc.eurEquivalent + " EUREQ");
	} else {
	    newAcc.setBtc(0);
	    newAcc.setEur(acc.getEur());
	    newAcc.setEurEquivalent(newAcc.getEur());
	    System.out.println("hold @ " + currentPrice + ". got " + newAcc.btc + " BTC, " + newAcc.eur + " EUR, "
		    + newAcc.eurEquivalent + " EUREQ");
	}
	return newAcc;
    }

    private double getCurrentPrice(InputDataSnapshot snapshot) {
	return snapshot.currentRate.getClose();
    }

    private double[] buildInputArray(InputDataSnapshot state, Account account) {
	List<StateEdgePart> parts = state.getAllStateParts().subList(state.getPartsStartIndex(),
		state.getPartsEndIndex());

	double[] result = new double[2 * parts.size() + 2];

	// Check if at least 2 parts
	for (int i = 0; i < parts.size(); i++) {
	    StateEdgePart currentPart = parts.get(i);
	    result[2 * i + 0] = currentPart.getSentiment() != null ? currentPart.getSentiment().getPredictedDelta()
		    : 0.0;
	    result[2 * i + 1] = currentPart.getChartEntry().getClose();
	}

	result[2 * parts.size() + 0] = account.getBtc() > 0.0 ? 1.0 : 0.0;
	result[2 * parts.size() + 1] = account.getEur() > 0.0 ? 1.0 : 0.0;

	return result;
    }

    private Account buildAccount() {
	Account result = new Account();
	result.btc = 0;
	result.eur = 1000;
	result.eurEquivalent = 1000;
	return result;
    }

    private List<InputDataSnapshot> buildInputData(List<ChartEntry> chartEntries,
	    List<TradingEvaluationResult> sentiments) {
	List<StateEdgePart> parts = buildStateParts(chartEntries, sentiments);

	List<InputDataSnapshot> result = new ArrayList<>();
	for (int i = 0; i < parts.size() - 120; i++) {

	    result.add(buildInputDataSnapshot(i, i + 120, parts, chartEntries.get(i + 120)));
	}

	return result;
    }

    private InputDataSnapshot buildInputDataSnapshot(int startIndex, int endIndex, List<StateEdgePart> parts,
	    ChartEntry chartEntry) {
	InputDataSnapshot result = new InputDataSnapshot();
	result.allStateParts = parts;
	result.partsEndIndex = endIndex;
	result.partsStartIndex = startIndex;
	result.currentRate = chartEntry;

	return result;
    }

    private List<StateEdgePart> buildStateParts(List<ChartEntry> chartEntries,
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

    class InputDataSnapshot {
	public ChartEntry currentRate;
	private long id;
	private int partsStartIndex = 0;
	private int partsEndIndex = 0;
	private List<StateEdgePart> allStateParts;

	public long getId() {
	    return id;
	}

	public void setId(long id) {
	    this.id = id;
	}

	public int getPartsStartIndex() {
	    return partsStartIndex;
	}

	public void setPartsStartIndex(int partsStartIndex) {
	    this.partsStartIndex = partsStartIndex;
	}

	public int getPartsEndIndex() {
	    return partsEndIndex;
	}

	public void setPartsEndIndex(int partsEndIndex) {
	    this.partsEndIndex = partsEndIndex;
	}

	public List<StateEdgePart> getAllStateParts() {
	    return allStateParts;
	}

	public void setAllStateParts(List<StateEdgePart> allStateParts) {
	    this.allStateParts = allStateParts;
	}

    }

    class Account {
	private double btc;
	private double eur;
	private double eurEquivalent;

	public double getBtc() {
	    return btc;
	}

	public void setBtc(double btc) {
	    this.btc = btc;
	}

	public double getEur() {
	    return eur;
	}

	public void setEur(double eur) {
	    this.eur = eur;
	}

	public double getEurEquivalent() {
	    return eurEquivalent;
	}

	public void setEurEquivalent(double eurEquivalent) {
	    this.eurEquivalent = eurEquivalent;
	}

    }

    public class StateEdgePart {
	private TradingEvaluationResult sentiment;
	private DeltaChartEntry chartEntry;

	public TradingEvaluationResult getSentiment() {
	    return sentiment;
	}

	public void setSentiment(TradingEvaluationResult sentiment) {
	    this.sentiment = sentiment;
	}

	public DeltaChartEntry getChartEntry() {
	    return chartEntry;
	}

	public void setChartEntry(DeltaChartEntry chartEntry) {
	    this.chartEntry = chartEntry;
	}

    }
}
