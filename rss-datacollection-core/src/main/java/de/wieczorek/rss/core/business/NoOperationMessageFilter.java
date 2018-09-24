package de.wieczorek.rss.core.business;

import de.wieczorek.rss.types.RssEntry;

public class NoOperationMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {

	return true;
    }

}
