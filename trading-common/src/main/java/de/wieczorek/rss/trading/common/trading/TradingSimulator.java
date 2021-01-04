package de.wieczorek.rss.trading.common.trading;

import de.wieczorek.rss.trading.common.io.*;
import de.wieczorek.rss.trading.common.oracle.Oracle;
import de.wieczorek.rss.trading.common.oracle.OracleInput;
import de.wieczorek.rss.trading.common.oracle.TraderState;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;
import de.wieczorek.rss.trading.types.StateEdgeChainMetaInfo;
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
    private SimulationContextProvider contextProvider;

    public List<Trade> simulate(Oracle oracle) {
        contextProvider.createContext();
        DataGenerator generator = dataGeneratorBuilder.produceGenerator();
        List<Trade> result = simulate(generator, oracle).getTrades();
        contextProvider.destroyContext();
        return result;
    }


    public TradingSimulationResult simulate(StateEdgeChainMetaInfo metaInfo, DataGenerator generator, Oracle oracle) {
        StateEdge startState = generator.buildNewStartState(metaInfo);
        contextProvider.createContext();
        TradingSimulationResult result = simulate(startState, generator, oracle);
        contextProvider.destroyContext();
        return result;
    }

    public TradingSimulationResult simulate(DataGenerator generator, Oracle oracle) {
        StateEdge startState = generator.buildNewStartState(0);
        contextProvider.createContext();
        TradingSimulationResult result = simulate(startState, generator, oracle);
        contextProvider.destroyContext();
        return result;
    }

    public TradingSimulationResult simulate(StateEdge startState, DataGenerator generator, Oracle oracle) {

        StateEdge current = startState;
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

            SimulationContext context = contextProvider.getContext();
            if (nextAction == ActionVertexType.BUY) {
                context.setLastBuyTime(getCurrentTime(current));
            } else {
                context.setLastBuyTime(null);
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

        if (action == ActionVertexType.SELL) {
            newTrade.setAction(ActionVertexType.SELL);
            trades.add(newTrade);
            input.setState(new TraderState());
        } else if (action == ActionVertexType.BUY) {
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

    private LocalDateTime getCurrentTime(StateEdge snapshot) {
        return snapshot.getAllStateParts().get(snapshot.getPartsStartIndex()).getChartEntry().getDate();
    }

}
