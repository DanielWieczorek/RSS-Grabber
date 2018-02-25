package de.wieczorek.rss.core;

public class NoOperationMessageTransformer implements MessageTransformer {

    @Override
    public RssEntry apply(RssEntry t) {
	return t;
    }

}
