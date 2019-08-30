package de.wieczorek.chart.core.business;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class OhlcApiRequestResult {

    @JsonProperty("error")
    private List<Object> errors;

    @JsonProperty("result")
    private Map<String, Object> results;

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

}
