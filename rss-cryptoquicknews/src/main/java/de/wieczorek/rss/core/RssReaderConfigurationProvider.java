package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @FeedUrl
    private String feedUrl = "http://www.cryptoquicknews.com/feed/";

    @Produces
    @RestPort
    private int restPort = 8040;

    @Produces
    private MessageFilter filter = new NoOperationMessageFilter();

    @Produces
    private MessageTransformer transformer = new CryptoquicknewsMessageTransformer();
}
