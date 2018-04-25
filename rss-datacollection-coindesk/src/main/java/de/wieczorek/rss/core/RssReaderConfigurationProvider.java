package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.rss.core.business.MessageFilter;
import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.NoOperationMessageTransformer;
import de.wieczorek.rss.core.config.FeedUrl;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @ServiceName
    private String serviceName = "rss-collector-coindesk";

    @Produces
    @FeedUrl
    private String feedUrl = "http://feeds.feedburner.com/CoinDesk";

    @Produces
    @RestPort
    private int restPort = 8000;

    @Produces
    private MessageFilter filter = new NoOperationMessageFilter();

    @Produces
    private MessageTransformer transformer = new NoOperationMessageTransformer();
}
