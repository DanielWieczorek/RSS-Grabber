package de.wieczorek.rss.types.ui;

import de.wieczorek.rss.types.RssEntry;

import java.util.List;

public interface CallableResource {
    List<RssEntry> entriesFromLast24h();

    List<RssEntry> entriesAfter(long unixTimestamp);

    List<RssEntry> allEntries();
}
