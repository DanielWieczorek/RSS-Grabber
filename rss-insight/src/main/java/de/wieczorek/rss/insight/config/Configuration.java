package de.wieczorek.rss.insight.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 10020;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;
}
