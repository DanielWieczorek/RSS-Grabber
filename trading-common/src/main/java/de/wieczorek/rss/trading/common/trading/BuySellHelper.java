package de.wieczorek.rss.trading.common.trading;

import de.wieczorek.rss.trading.types.Account;

public final class BuySellHelper {

    public static final double SELL_OFFSET_ABSOLUTE = 10.0;
    public static final double BUY_OFFSET_ABSOLUTE = 5.0;
    private static final double FEES_PERCENT = 0.3;

    private BuySellHelper() {

    }

    public static Account processBuy(double currentPrice, Account acc) {
        Account newAcc = new Account();

        newAcc.setBtc((acc.getEur() / (currentPrice + BUY_OFFSET_ABSOLUTE)) * remainingPercent(FEES_PERCENT));
        newAcc.setEur(0);
        newAcc.setEurEquivalent(newAcc.getBtc() * currentPrice);

        return newAcc;
    }

    private static double remainingPercent(double fees) {
        return (100 - fees) / 100;
    }

    public static Account processSell(double currentPrice, Account acc) {
        Account newAcc = new Account();

        newAcc.setBtc(0);
        newAcc.setEur(acc.getBtc() * (currentPrice - SELL_OFFSET_ABSOLUTE) * remainingPercent(FEES_PERCENT));
        newAcc.setEurEquivalent(newAcc.getEur());

        return newAcc;
    }

    public static Account processNoop(double currentPrice, Account acc) {
        Account newAcc = new Account();

        newAcc.setBtc(acc.getBtc());
        newAcc.setEur(acc.getEur());
        newAcc.setEurEquivalent(newAcc.getEur() + newAcc.getBtc() * currentPrice);

        return newAcc;
    }
}
