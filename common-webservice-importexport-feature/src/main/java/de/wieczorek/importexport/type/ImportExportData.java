package de.wieczorek.importexport.type;

import java.util.Collection;
import java.util.Map;

public class ImportExportData {

    private Map<String, Collection> data;

    public Map<String, Collection> getData() {
        return data;
    }

    public void setData(Map<String, Collection> data) {
        this.data = data;
    }

}
