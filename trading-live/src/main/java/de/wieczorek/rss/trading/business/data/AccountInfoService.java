package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.types.Account;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.utils.OrderValuesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;

@ApplicationScoped
public class AccountInfoService {
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoService.class);

    @Inject
    private Exchange exchange;

    private OrderValuesHelper helper;

    private Object lock;

    @PostConstruct
    private void initialize() {
        CurrencyPairMetaData metadata = exchange.getExchangeMetaData().getCurrencyPairs().get(CurrencyPair.BTC_EUR);
        helper = new OrderValuesHelper(metadata);
    }

    public synchronized Account getAccount() {
        try {
            AccountInfo info = exchange.getAccountService().getAccountInfo();
            Ticker last = exchange.getMarketDataService().getTicker(CurrencyPair.BTC_EUR);

            Account acc = new Account();
            acc.setBtc(info.getWallets().get(null).getBalance(Currency.BTC).getAvailable().doubleValue());
            acc.setEur(info.getWallets().get(null).getBalance(Currency.EUR).getAvailable().doubleValue());
            acc.setEurEquivalent(info.getWallets().get(null).getBalance(Currency.EUR).getAvailable().doubleValue()
                    + acc.getBtc() * last.getLast().doubleValue());


            if (helper.amountUnderMinimum(new BigDecimal(acc.getBtc()))) {
                acc.setBtc(0);
            }

            if (helper.amountUnderMinimum(new BigDecimal(acc.getEur()))) {
                acc.setEur(0);
            }

            if (helper.amountUnderMinimum(new BigDecimal(acc.getEurEquivalent()))) {
                acc.setEurEquivalent(0);
            }

            return acc;
        } catch (IOException e) {
            logger.error("error while retrieving account balance ", e);
        }
        return null;

    }

}
