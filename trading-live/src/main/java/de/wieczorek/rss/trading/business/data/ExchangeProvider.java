package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.config.ServiceConfiguration;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.kraken.KrakenExchange;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;


@ApplicationScoped
public class ExchangeProvider {

    @Inject
    private ServiceConfiguration config;

    @Produces
    @ApplicationScoped
    private Exchange provideExchange() {
        ExchangeSpecification exSpec = new KrakenExchange().getDefaultExchangeSpecification();
        exSpec.setUserName(config.username);
        exSpec.setApiKey(config.apiKey);
        exSpec.setSecretKey(config.secretKey);

        return ExchangeFactory.INSTANCE.createExchange(exSpec);

    }
}
