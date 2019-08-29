package de.wieczorek.rss.core.config;

import de.wieczorek.rss.core.business.MessageFilter;
import de.wieczorek.rss.core.business.MessageTransformer;

public abstract class RssConfig {

    protected String serviceName;

    protected String feedUrl;

    protected MessageFilter filter;

    protected MessageTransformer transformer;

    public String getServiceName() {
        return serviceName;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public MessageFilter getFilter() {
        return filter;
    }

    public MessageTransformer getTransformer() {
        return transformer;
    }

}
