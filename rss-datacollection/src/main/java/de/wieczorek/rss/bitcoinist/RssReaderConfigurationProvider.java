package de.wieczorek.rss.bitcoinist;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-bitcoinist";
	feedUrl = "https://news.bitcoin.com/feed/";
	filter = new NoOperationMessageFilter();
	transformer = new BitcoinistMessageTransformer();
    }

}
