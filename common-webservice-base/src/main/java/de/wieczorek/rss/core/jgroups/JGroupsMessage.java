package de.wieczorek.rss.core.jgroups;

public class JGroupsMessage<T> {

    public Class<T> type;

    public T payload;
}
