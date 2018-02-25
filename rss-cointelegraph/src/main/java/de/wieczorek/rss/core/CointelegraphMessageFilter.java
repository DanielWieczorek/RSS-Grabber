package de.wieczorek.rss.core;

public class CointelegraphMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {
	return t.getDescription().contains("#NEWS");
    }

}
