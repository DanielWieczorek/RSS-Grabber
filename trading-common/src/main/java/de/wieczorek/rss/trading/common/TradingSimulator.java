package de.wieczorek.rss.trading.common;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TradingSimulator {

    private static final Logger logger = LoggerFactory.getLogger(TradingSimulator.class);

    @Inject
    private DataLoader dataLoader;

    @Inject
    private DataGeneratorBuilder dataGeneratorBuilder;

    @Inject
    private MetricNormalizer normalizer;

    public List<Trade> simulate(Oracle oracle) {

        DataGenerator generator =  dataGeneratorBuilder.produceGenerator();
        return simulate(generator,oracle);
    }

    public List<Trade> simulate(DataGenerator generator,Oracle oracle) {

        StateEdge current = generator.buildNewStartState(0);
        List<Trade> trades = new ArrayList<>();

        for (int i = 0; i < generator.getMaxIndex(); i += 1) {
            StateEdge next = performTrade(oracle, current, generator);
            if (next == null) {
                break;
            }
            addTrade(trades, current.getAccount(), next.getAccount(), current);
            current = next;
        }

        return trades;
    }

    private void addTrade(List<Trade> trades, Account account, Account newAccount, StateEdge snapshot) {

        Trade newTrade = new Trade();
        newTrade.setDate(snapshot.getAllStateParts().get(snapshot.getPartsEndIndex()).getChartEntry().getDate());
        newTrade.setBefore(account);
        newTrade.setAfter(newAccount);
        newTrade.setCurrentRate(getCurrentPrice(snapshot));

        if (account.getBtc() > newAccount.getBtc()) { // Sell
            newTrade.setAction(ActionVertexType.SELL);
            trades.add(newTrade);
        } else if (account.getBtc() < newAccount.getBtc()) { // Buy
            newTrade.setAction(ActionVertexType.BUY);
            trades.add(newTrade);
        }
    }

    private StateEdge performTrade(Oracle oracle, StateEdge snapshot,DataGenerator generator) {
        return generator.buildNextState(snapshot, oracle.nextAction(snapshot));
    }

    private double getCurrentPrice(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getClose();
    }

    private LocalDateTime getCurrentDateTime(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getDate();
    }

}
