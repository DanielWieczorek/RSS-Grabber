package de.wieczorek.rss.core.business;

import java.util.function.Function;

public interface MessageTransformer extends Function<RssEntry, RssEntry> {

}
