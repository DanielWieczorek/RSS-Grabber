package de.wieczorek.rss.core;

public class NoOperationMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {

	return true;
    }

}
