package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.types.Account;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
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
    private static final Logger logger = LoggerFactory.getLogger(Trader.class);

    @Inject
    private Exchange exchange;

    private OrderValuesHelper helper;

    @PostConstruct
    private void initialize() {
        CurrencyPairMetaData metadata = exchange.getExchangeMetaData().getCurrencyPairs().get(CurrencyPair.BTC_EUR);
        helper = new OrderValuesHelper(metadata);
    }

    public Account getAccount() {
        try {
            AccountInfo info = exchange.getAccountService().getAccountInfo();
            Account acc = new Account();
            acc.setBtc(info.getWallets().get(null).getBalance(Currency.BTC).getAvailable().doubleValue());
            acc.setEur(info.getWallets().get(null).getBalance(Currency.EUR).getAvailable().doubleValue());

            if (helper.amountUnderMinimum(new BigDecimal(acc.getBtc()))) {
                acc.setBtc(0);
            }

            if (helper.amountUnderMinimum(new BigDecimal(acc.getEur()))) {
                acc.setEur(0);
            }

            return acc;
        } catch (IOException e) {
            logger.error("error while retrieving account balance ", e);
        }
        return null;

    }

}
