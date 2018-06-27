package de.wieczorek.rss.bitcoinmagazine;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-bitcoinmagazine";
	feedUrl = "https://bitcoinmagazine.com/feed/";
	filter = new BitcoinmagazineMessageFilter();
	transformer = new BitcoinmagazineMessageTransformer();
    }
}
