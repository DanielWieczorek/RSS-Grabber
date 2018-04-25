package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.rss.core.business.MessageFilter;
import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.core.config.FeedUrl;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @ServiceName
    private String serviceName = "rss-collector-cointelegraph";

    @Produces
    @FeedUrl
    private String feedUrl = "https://cointelegraph.com/rss";

    @Produces
    @RestPort
    private int restPort = 8010;

    @Produces
    private MessageFilter filter = new CointelegraphMessageFilter();

    @Produces
    private MessageTransformer transformer = new CointelegraphMessageTransformer();
}
