package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig{

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
