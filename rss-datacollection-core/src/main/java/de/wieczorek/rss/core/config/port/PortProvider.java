package de.wieczorek.rss.core.config.port;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class PortProvider {

    @Inject
    @RestPort
    private int restPort;

    @Produces
    @JGroupsPort
    public int getJGroupsPort() {
	return restPort + 1;
    }
}
