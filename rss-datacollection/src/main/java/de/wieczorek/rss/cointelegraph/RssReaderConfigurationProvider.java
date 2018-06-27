package de.wieczorek.rss.cointelegraph;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.config.RssConfig;

@ApplicationScoped
public class RssReaderConfigurationProvider extends RssConfig {

    public RssReaderConfigurationProvider() {
	serviceName = "rss-collector-cointelegraph";
	feedUrl = "https://cointelegraph.com/rss";
	filter = new CointelegraphMessageFilter();
	transformer = new CointelegraphMessageTransformer();
    }
}
