package de.wieczorek.rss.trading.common.trading;

import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.io.DataLoader;
import de.wieczorek.rss.trading.common.oracle.Oracle;
import de.wieczorek.rss.trading.common.oracle.OracleInput;
import de.wieczorek.rss.trading.common.oracle.TraderState;
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

    public List<Trade> simulate(Oracle oracle) {

        DataGenerator generator = dataGeneratorBuilder.produceGenerator();
        return simulate(generator, oracle).getTrades();
    }

    public TradingSimulationResult simulate(DataGenerator generator, Oracle oracle) {

        StateEdge current = generator.buildNewStartState(0);
        List<Trade> trades = new ArrayList<>();

        OracleInput input = new OracleInput();
        input.setState(new TraderState());
        input.setStateEdge(current);
        for (int i = 0; i < generator.getMaxIndex(); i += 1) {

            ActionVertexType nextAction = oracle.nextAction(input).getDecision();
            StateEdge next = performTrade(input, generator, nextAction);
            if (next == null) {
                break;
            }
            addTrade(trades, current.getAccount(), next.getAccount(), input, nextAction);
            current = next;
            input.setStateEdge(current);
        }

        TradingSimulationResult result = new TradingSimulationResult();
        result.setTrades(trades);
        result.setFinalBalance(current.getAccount());


        return result;
    }

    private void addTrade(List<Trade> trades, Account account, Account newAccount, OracleInput input, ActionVertexType action) {

        Trade newTrade = new Trade();
        newTrade.setDate(input.getStateEdge().getAllStateParts().get(input.getStateEdge().getPartsEndIndex()).getChartEntry().getDate());
        newTrade.setBefore(account);
        newTrade.setAfter(newAccount);
        newTrade.setCurrentRate(getCurrentPrice(input.getStateEdge()));

        if (action == ActionVertexType.SELL) { // Sell
            newTrade.setAction(ActionVertexType.SELL);
            trades.add(newTrade);
            input.setState(new TraderState());
        } else if (action == ActionVertexType.BUY) { // Buy
            newTrade.setAction(ActionVertexType.BUY);
            trades.add(newTrade);
            TraderState newState = new TraderState();
            newState.setLastBuyTime(newTrade.getDate());
            newState.setLastBuyPrice(newTrade.getCurrentRate());
            input.setState(newState);
        }
    }

    private StateEdge performTrade(OracleInput input, DataGenerator generator, ActionVertexType action) {
        return generator.buildNextState(input.getStateEdge(), action);
    }

    private double getCurrentPrice(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getClose();
    }

    private LocalDateTime getCurrentDateTime(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getDate();
    }

}
