package de.wieczorek.rss.core.feature;

import java.util.List;

public class FeatureDescriptor {

    private FeatureType type;

    private String description;

    private List<FeatureAction> actions;

    public FeatureType getType() {
	return type;
    }

    public void setType(FeatureType type) {
	this.type = type;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<FeatureAction> getActions() {
	return actions;
    }

    public void setActions(List<FeatureAction> actions) {
	this.actions = actions;
    }

}
