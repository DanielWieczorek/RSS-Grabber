package de.wieczorek.rss.core.business;

import de.wieczorek.rss.types.RssEntry;

import java.util.function.Predicate;

public interface MessageFilter extends Predicate<RssEntry> {

}
