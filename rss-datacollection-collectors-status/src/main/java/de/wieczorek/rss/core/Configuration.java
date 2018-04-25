package de.wieczorek.rss.core;

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
}
