package de.wieczorek.rss.trading.ui;

public class ActionVertex {
    private StateEdge targetState;
    private ActionVertexType type;

    public StateEdge getTargetState() {
	return targetState;
    }

    public void setTargetState(StateEdge targetState) {
	this.targetState = targetState;
    }

    public ActionVertexType getType() {
	return type;
    }

    public void setType(ActionVertexType type) {
	this.type = type;
    }

}