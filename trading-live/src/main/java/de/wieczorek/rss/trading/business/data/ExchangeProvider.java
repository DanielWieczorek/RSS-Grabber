package de.wieczorek.rss.trading.business.data;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.kraken.KrakenExchange;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;


@ApplicationScoped
public class ExchangeProvider {

    private static final String API_KEY = "29Efgoh1Mx4mEIqw4FZJb8N/7KwPY66NKwOT+wSGJCMZD5hXWSO6T/sB";

    private static final String PRIVATE_KEY = "AyaFulGHTz9u5/iwB3GiKz1htreEqUpgSoc8S+DzYiV8s6CVHL8cA7izCzksoMn6Wn+0Ns4STi0VulbaYzxeGA==";


    @Produces
    @ApplicationScoped
    private Exchange provideExchange() {
        ExchangeSpecification exSpec = new KrakenExchange().getDefaultExchangeSpecification();
        exSpec.setUserName("DWI2304");
        exSpec.setApiKey(API_KEY);
        exSpec.setSecretKey(PRIVATE_KEY);

        return ExchangeFactory.INSTANCE.createExchange(exSpec);

    }
}
