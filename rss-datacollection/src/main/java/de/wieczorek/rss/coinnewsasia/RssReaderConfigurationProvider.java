package de.wieczorek.rss.coinnewsasia;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-coinnewsasia";
	feedUrl = "http://www.coinnewsasia.com/feed/";
	filter = new NoOperationMessageFilter();
	transformer = new CoinnewsasiaMessageTransformer();
    }
}
