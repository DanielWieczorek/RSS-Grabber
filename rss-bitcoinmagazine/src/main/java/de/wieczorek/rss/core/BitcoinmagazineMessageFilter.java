package de.wieczorek.rss.core;

public class BitcoinmagazineMessageFilter implements MessageFilter {

    @Override
    public boolean test(RssEntry t) {
	return !t.getDescription().contains("Week in Review");
    }

}
