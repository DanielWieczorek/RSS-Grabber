package de.wieczorek.chart.core.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 32000;

    @Produces
    @JGroupsPort
    public int getJGroupsPort() {
	return restPort + 1;
    }

    @Produces
    @ServiceName
    private String serviceName = "microservice-authentication";
}
