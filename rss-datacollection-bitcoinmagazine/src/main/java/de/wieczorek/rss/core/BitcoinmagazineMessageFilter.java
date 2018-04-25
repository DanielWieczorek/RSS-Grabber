package de.wieczorek.rss.core;

import de.wieczorek.rss.core.business.MessageFilter;
import de.wieczorek.rss.core.business.RssEntry;

public class BitcoinmagazineMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {
	return !t.getDescription().contains("Week in Review");
    }

}
