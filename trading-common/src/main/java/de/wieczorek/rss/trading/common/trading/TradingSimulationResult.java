package de.wieczorek.rss.trading.common.trading;

import de.wieczorek.rss.trading.types.Account;

import java.util.List;

public class TradingSimulationResult {
    private List<Trade> trades;
    private Account finalBalance;
    private Account initialBalance;

    public Account getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Account initialBalance) {
        this.initialBalance = initialBalance;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public Account getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Account finalBalance) {
        this.finalBalance = finalBalance;
    }
}
