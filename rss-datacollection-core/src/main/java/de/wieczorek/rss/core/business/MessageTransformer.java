package de.wieczorek.rss.core.business;

import java.util.function.Function;

import de.wieczorek.rss.types.RssEntry;

public interface MessageTransformer extends Function<RssEntry, RssEntry> {

}
