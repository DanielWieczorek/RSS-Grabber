package de.wieczorek.rss.trading.business;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import de.wieczorek.rss.trading.ui.StateEdge;

public class IndexedStateGraph {
    private Map<Long, StateEdge> idEdgeMapping = new HashMap<>();

    public IndexedStateGraph(StateEdge rootEdge) {
	loadTree(rootEdge);
    }

    private void loadTree(StateEdge rootEdge) {
	LinkedList<StateEdge> stack = new LinkedList<>();
	stack.add(rootEdge);

	while (!stack.isEmpty()) {
	    StateEdge current = stack.removeLast();

	    idEdgeMapping.put(current.getId(), current);
	    current.getActions().stream().map(action -> action.getTargetState()).filter(Objects::nonNull)
		    .forEach(stack::add);

	}

    }

    public StateEdge getStateEdge(long id) {
	return idEdgeMapping.get(id);
    }
}
