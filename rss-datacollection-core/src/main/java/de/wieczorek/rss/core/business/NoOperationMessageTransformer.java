package de.wieczorek.rss.core.business;

import de.wieczorek.rss.types.RssEntry;

public class NoOperationMessageTransformer implements MessageTransformer {

    @Override
    public RssEntry apply(RssEntry t) {
        return t;
    }

}
