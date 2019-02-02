package de.wieczorek.rss.core.feature;

public class FeatureAction {
    private String name;

    private String path;

    private String method;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public FeatureAction() {

    }

    public FeatureAction(String name, String path, String method) {
	super();
	this.name = name;
	this.path = path;
	this.method = method;
    }

}
