package de.wieczorek.rss.core.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class PortProvider {

    @Produces
    @RestPort
    private int restPort = 8020;

    @Produces
    @JGroupsPort
    public int getJGroupsPort() {
	return restPort + 1;
    }
}
