package de.wieczorek.rss.bitcoinmagazine;

import de.wieczorek.rss.core.business.MessageFilter;
import de.wieczorek.rss.types.RssEntry;

public class BitcoinmagazineMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {
	return !t.getDescription().contains("Week in Review");
    }

}
