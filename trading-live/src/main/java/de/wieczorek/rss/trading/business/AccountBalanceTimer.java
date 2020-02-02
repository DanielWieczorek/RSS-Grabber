package de.wieczorek.rss.trading.business;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import de.wieczorek.rss.trading.business.data.AccountInfoService;
import de.wieczorek.rss.trading.persistence.LiveAccount;
import de.wieczorek.rss.trading.persistence.LiveAccountDao;
import de.wieczorek.rss.trading.types.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class AccountBalanceTimer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AccountBalanceTimer.class);

    @Inject
    private LiveAccountDao dao;

    @Inject
    private AccountInfoService infoService;

    @Override
    public void run() {
        try {
            logger.debug("Triggering account update.");

            Account accFromExchange = infoService.getAccount();

            LiveAccount account = new LiveAccount();
            account.setTime(LocalDateTime.now());
            account.setBtc(accFromExchange.getBtc());
            account.setEur(accFromExchange.getEur());
            account.setEurEquivalent(accFromExchange.getEurEquivalent());
            dao.addAccountUpdate(account);
        } catch (Exception e) {
            logger.error("error while triggering account update: ", e);
        }
    }
}
