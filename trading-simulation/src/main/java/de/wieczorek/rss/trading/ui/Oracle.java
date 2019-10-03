package de.wieczorek.rss.trading.ui;

import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.StateEdge;

public interface Oracle {
    ActionVertexType nextAction(StateEdge snapshot);
}
