package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

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
