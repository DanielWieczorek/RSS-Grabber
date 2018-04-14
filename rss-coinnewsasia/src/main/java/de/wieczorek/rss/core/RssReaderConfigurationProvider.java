package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RssReaderConfigurationProvider implements RssConfig {

    @Produces
    @ServiceName
    private String serviceName = "rss-collector-coinnewsasia";

    @Produces
    @FeedUrl
    private String feedUrl = "http://www.coinnewsasia.com/feed/";

    @Produces
    @RestPort
    private int restPort = 8050;

    @Produces
    private MessageFilter filter = new NoOperationMessageFilter();

    @Produces
    private MessageTransformer transformer = new CoinnewsasiaMessageTransformer();
}
