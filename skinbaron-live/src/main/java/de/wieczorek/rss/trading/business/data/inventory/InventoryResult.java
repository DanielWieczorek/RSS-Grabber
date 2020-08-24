package de.wieczorek.rss.trading.business.data.inventory;

import java.util.List;

public class InventoryResult {
    private List<InventoryResultItem> items;
    private List<String> generalErrors;
    private int openSendGroupCount;

    public int getOpenSendGroupCount() {
        return openSendGroupCount;
    }

    public void setOpenSendGroupCount(int openSendGroupCount) {
        this.openSendGroupCount = openSendGroupCount;
    }

    public List<InventoryResultItem> getItems() {
        return items;
    }

    public void setItems(List<InventoryResultItem> items) {
        this.items = items;
    }

    public List<String> getGeneralErrors() {
        return generalErrors;
    }

    public void setGeneralErrors(List<String> generalErrors) {
        this.generalErrors = generalErrors;
    }
}
