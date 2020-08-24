package de.wieczorek.rss.trading.business.data.price;

import java.util.List;

public class StackableAvailaiblityTableResult {
    private List<StackableAvailaiblityTableResultItem> rows;

    public List<StackableAvailaiblityTableResultItem> getRows() {
        return rows;
    }

    public void setRows(List<StackableAvailaiblityTableResultItem> rows) {
        this.rows = rows;
    }
}
