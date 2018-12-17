package de.wieczorek.rss.trading.business.data;

import java.util.ArrayList;
import java.util.List;

public class StateEdge {
    private long id;
    private int partsStartIndex = 0;
    private int partsEndIndex = 0;
    private Account account;
    private StateEdge previous;
    private List<StateEdgePart> allStateParts;

    private List<ActionVertex> actions = new ArrayList<>();

    public Account getAccount() {
	return account;
    }

    public void setAccount(Account account) {
	this.account = account;
    }

    public List<ActionVertex> getActions() {
	return actions;
    }

    public void setActions(List<ActionVertex> actions) {
	this.actions = actions;
    }

    public int getPartsStartIndex() {
	return partsStartIndex;
    }

    public void setPartsStartIndex(int partsStartIndex) {
	this.partsStartIndex = partsStartIndex;
    }

    public int getPartsEndIndex() {
	return partsEndIndex;
    }

    public void setPartsEndIndex(int partsEndIndex) {
	this.partsEndIndex = partsEndIndex;
    }

    public StateEdge getPrevious() {
	return previous;
    }

    public void setPrevious(StateEdge previous) {
	this.previous = previous;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public List<StateEdgePart> getAllStateParts() {
	return allStateParts;
    }

    public void setAllStateParts(List<StateEdgePart> allStateParts) {
	this.allStateParts = allStateParts;
    }

}