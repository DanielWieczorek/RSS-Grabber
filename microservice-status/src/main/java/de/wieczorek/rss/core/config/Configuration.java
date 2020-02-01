package de.wieczorek.rss.core.config;

import de.wieczorek.core.config.ServiceName;
import de.wieczorek.core.config.port.JGroupsPort;
import de.wieczorek.core.config.port.RestPort;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 10000;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;

    @Produces
    @ServiceName
    private String serviceName = "microservice-status";
}
