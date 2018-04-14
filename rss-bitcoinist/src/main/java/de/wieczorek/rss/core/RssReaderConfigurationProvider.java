package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @ServiceName
    private String serviceName = "rss-collector-bitcoinist";

    @Produces
    @FeedUrl
    private String feedUrl = "https://news.bitcoin.com/feed/";

    @Produces
    @RestPort
    private int restPort = 8020;

    @Produces
    private MessageFilter filter = new NoOperationMessageFilter();

    @Produces
    private MessageTransformer transformer = new BitcoinistMessageTransformer();
}
