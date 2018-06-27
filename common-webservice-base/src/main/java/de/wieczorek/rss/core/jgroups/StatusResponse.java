package de.wieczorek.rss.core.jgroups;

public class StatusResponse {

    private String collectorName;

    private String bindHostname;

    private int bindPort;

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

    public String getCollectorName() {
	return collectorName;
    }

    public void setCollectorName(String collectorName) {
	this.collectorName = collectorName;
    }

}
