package de.wieczorek.rss.core.business;

import java.util.function.Predicate;

import de.wieczorek.rss.types.RssEntry;

public interface MessageFilter extends Predicate<RssEntry> {

}
