package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @FeedUrl
    private String feedUrl = "https://bitcoinmagazine.com/feed/";

    @Produces
    @RestPort
    private int restPort = 8070;

    @Produces
    private MessageFilter filter = new BitcoinmagazineMessageFilter();

    @Produces
    private MessageTransformer transformer = new BitcoinmagazineMessageTransformer();
}
