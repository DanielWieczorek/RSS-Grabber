package de.wieczorek.rss.coindesk;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.NoOperationMessageTransformer;
import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-coindesk";
	feedUrl = "http://feeds.feedburner.com/CoinDesk";
	filter = new NoOperationMessageFilter();
	transformer = new NoOperationMessageTransformer();
    }
}
