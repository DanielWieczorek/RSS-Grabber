package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @ServiceName
    private String serviceName = "rss-collector-cryptonewsnet";

    @Produces
    @FeedUrl
    private String feedUrl = "https://www.crypto-news.net/feed/";

    @Produces
    @RestPort
    private int restPort = 8030;

    @Produces
    private MessageFilter filter = new NoOperationMessageFilter();

    @Produces
    private MessageTransformer transformer = new NoOperationMessageTransformer();
}
