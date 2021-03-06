package de.wieczorek.rss.core.jgroups;

import de.wieczorek.core.feature.FeatureDescriptor;

import java.util.List;

public class ServiceMetadata {

    private String name;

    private String bindHostname;

    private int bindPort;

    private String status;

    private List<FeatureDescriptor> features;

    public String getBindHostname() {
        return bindHostname;
    }

    public void setBindHostname(String bindHostname) {
        this.bindHostname = bindHostname;
    }

    public int getBindPort() {
        return bindPort;
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String collectorName) {
        this.name = collectorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FeatureDescriptor> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureDescriptor> features) {
        this.features = features;
    }

}
