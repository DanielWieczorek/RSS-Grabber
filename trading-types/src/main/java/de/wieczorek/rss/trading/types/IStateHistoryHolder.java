package de.wieczorek.rss.trading.types;

import java.util.List;

public interface IStateHistoryHolder {
    public int getPartsStartIndex();

    public int getPartsEndIndex();

    public List<StateEdgePart> getAllStateParts();
}
