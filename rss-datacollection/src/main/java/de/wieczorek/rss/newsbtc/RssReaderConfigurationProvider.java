package de.wieczorek.rss.newsbtc;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-newsbtc";
	feedUrl = "https://www.newsbtc.com/feed/";
	filter = new NoOperationMessageFilter();
	transformer = new NewsbtcMessageTransformer();
    }
}
