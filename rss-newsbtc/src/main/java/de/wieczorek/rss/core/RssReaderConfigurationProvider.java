package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @FeedUrl
    private String feedUrl = "https://www.newsbtc.com/feed/";

    @Produces
    @RestPort
    private int restPort = 8060;

    @Produces
    private MessageFilter filter = new NoOperationMessageFilter();

    @Produces
    private MessageTransformer transformer = new NewsbtcMessageTransformer();
}
