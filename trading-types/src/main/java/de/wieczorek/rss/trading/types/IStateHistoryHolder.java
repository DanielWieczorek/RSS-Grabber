package de.wieczorek.rss.trading.types;

import java.util.List;

public interface IStateHistoryHolder {
     int getPartsStartIndex();

     int getPartsEndIndex();

     List<StateEdgePart> getAllStateParts();
}
