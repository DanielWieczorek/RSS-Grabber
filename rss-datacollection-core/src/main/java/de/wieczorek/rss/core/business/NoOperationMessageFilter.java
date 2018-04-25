package de.wieczorek.rss.core.business;

public class NoOperationMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {

	return true;
    }

}
