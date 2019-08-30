package de.wieczorek.rss.core.business;

import de.wieczorek.rss.types.RssEntry;

import java.util.function.Function;

public interface MessageTransformer extends Function<RssEntry, RssEntry> {

}
