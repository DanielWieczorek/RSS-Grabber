package de.wieczorek.rss.trading.types;

import java.util.List;

public class StateEdge implements IStateHistoryHolder {
    private long id;
    private int partsStartIndex = 0;
    private int partsEndIndex = 0;
    private Account account;
    private List<StateEdgePart> allStateParts;

    private StateEdgeChainMetaInfo metaInfo;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public int getPartsStartIndex() {
        return partsStartIndex;
    }

    public void setPartsStartIndex(int partsStartIndex) {
        this.partsStartIndex = partsStartIndex;
    }

    @Override
    public int getPartsEndIndex() {
        return partsEndIndex;
    }

    public void setPartsEndIndex(int partsEndIndex) {
        this.partsEndIndex = partsEndIndex;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public List<StateEdgePart> getAllStateParts() {
        return allStateParts;
    }

    public void setAllStateParts(List<StateEdgePart> allStateParts) {
        this.allStateParts = allStateParts;
    }

    public StateEdgeChainMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(StateEdgeChainMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

}