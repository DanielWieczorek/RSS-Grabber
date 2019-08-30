package de.wieczorek.rss.trading.business.data;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.trading.types.IStateHistoryHolder;
import de.wieczorek.rss.trading.types.StateEdgePart;

import java.util.List;

public class InputDataSnapshot implements IStateHistoryHolder {
    private ChartEntry currentRate;
    private long id;
    private int partsStartIndex = 0;
    private int partsEndIndex = 0;
    private List<StateEdgePart> allStateParts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public List<StateEdgePart> getAllStateParts() {
        return allStateParts;
    }

    public void setAllStateParts(List<StateEdgePart> allStateParts) {
        this.allStateParts = allStateParts;
    }

    public ChartEntry getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(ChartEntry currentRate) {
        this.currentRate = currentRate;
    }

}