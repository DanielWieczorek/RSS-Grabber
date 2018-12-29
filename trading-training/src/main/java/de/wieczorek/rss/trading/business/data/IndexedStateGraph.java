package de.wieczorek.rss.trading.business.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IndexedStateGraph {
    private Map<Long, StateEdge> idEdgeMapping = new HashMap<>();
    private final StateEdge rootEdge;

    public IndexedStateGraph(StateEdge rootEdge) {
	this.rootEdge = rootEdge;
	loadTree(rootEdge);
    }

    private void loadTree(StateEdge rootEdge) { // TODO check id == 0 or update the handling of retrieving the root.
	LinkedList<StateEdge> stack = new LinkedList<>();
	stack.add(rootEdge);

	while (!stack.isEmpty()) {
	    StateEdge current = stack.removeLast();

	    idEdgeMapping.put(current.getId(), current);
	    current.getActions().stream().map(action -> action.getTargetState()).filter(Objects::nonNull)
		    .forEach(stack::add);

	}

    }

    public StateEdge getRootEdge() {
	return rootEdge;
    }

    public StateEdge getStateEdge(long id) {
	return idEdgeMapping.get(id);
    }

    public StateEdge getTerminalStateWithHighestValue() {
	Deque<StateEdge> edgesToEvaluate = new ArrayDeque<>();
	edgesToEvaluate.add(rootEdge);

	StateEdge currentlyBestCandidate = null;

	while (!edgesToEvaluate.isEmpty()) {
	    StateEdge newCandidate = edgesToEvaluate.pollLast();
	    if (isTerminalState(newCandidate)) {
		if (hasHigherValue(currentlyBestCandidate, newCandidate)) {
		    currentlyBestCandidate = newCandidate;
		}
		continue;
	    }
	    edgesToEvaluate.addAll(newCandidate.getActions().stream().map(action -> action.getTargetState())
		    .collect(Collectors.toList()));

	}
	return currentlyBestCandidate;
    }

    private boolean isTerminalState(StateEdge newCandidate) {
	return newCandidate.getActions().isEmpty();
    }

    private boolean hasHigherValue(StateEdge previous, StateEdge newCandidate) {
	return previous == null
		|| previous.getAccount().getEurEquivalent() < newCandidate.getAccount().getEurEquivalent();
    }
}
