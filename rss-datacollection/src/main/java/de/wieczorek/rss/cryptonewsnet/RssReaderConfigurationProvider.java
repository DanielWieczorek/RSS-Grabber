package de.wieczorek.rss.cryptonewsnet;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.NoOperationMessageTransformer;
import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-cryptonewsnet";
	feedUrl = "https://www.crypto-news.net/feed/";
	filter = new NoOperationMessageFilter();
	transformer = new NoOperationMessageTransformer();
    }
}
