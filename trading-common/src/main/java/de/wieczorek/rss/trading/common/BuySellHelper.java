package de.wieczorek.rss.trading.common;

import de.wieczorek.rss.trading.types.Account;

public final class BuySellHelper {

    private BuySellHelper() {

    }

    public static Account processBuy(double currentPrice, Account acc) {
	Account newAcc = new Account();
	if (acc.getBtc() > 0.0) {
	    newAcc.setBtc(acc.getBtc());
	    newAcc.setEur(0);
	    newAcc.setEurEquivalent(newAcc.getBtc() * currentPrice);
	} else {
	    newAcc.setBtc((acc.getEur() / currentPrice) * ((100 - 0.2) / 100));
	    newAcc.setEur(0);
	    newAcc.setEurEquivalent(newAcc.getBtc() * currentPrice);
	}
	return newAcc;
    }

    public static Account processSell(double currentPrice, Account acc) {
	Account newAcc = new Account();
	if (acc.getBtc() > 0.0) {
	    newAcc.setBtc(0);
	    newAcc.setEur(acc.getBtc() * currentPrice * ((100 - 0.2) / 100));
	    newAcc.setEurEquivalent(newAcc.getEur());
	} else {
	    newAcc.setBtc(0);
	    newAcc.setEur(acc.getEur());
	    newAcc.setEurEquivalent(newAcc.getEur());
	}
	return newAcc;
    }

}
