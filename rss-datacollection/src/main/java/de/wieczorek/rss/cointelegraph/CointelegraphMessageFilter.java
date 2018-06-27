package de.wieczorek.rss.cointelegraph;

import de.wieczorek.rss.core.business.MessageFilter;
import de.wieczorek.rss.core.business.RssEntry;

public class CointelegraphMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {
	return t.getDescription().contains("#NEWS");
    }

}
