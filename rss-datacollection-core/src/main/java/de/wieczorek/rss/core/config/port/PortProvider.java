package de.wieczorek.rss.core.config.port;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

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
