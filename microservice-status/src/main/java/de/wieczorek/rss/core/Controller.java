package de.wieczorek.rss.core;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.wieczorek.rss.core.jgroups.ServiceMetadata;
import de.wieczorek.rss.core.jgroups.StatusRequester;

@ApplicationScoped
public class Controller {

    @Inject
    private StatusRequester requester;

    public List<ServiceMetadata> status() {
        return requester.requestStates();
    }

    public List<ServiceMetadata> stopService(String collectorName) {
        requester.stop(collectorName);
        return requester.requestStates();
    }

    public List<ServiceMetadata> startService(String collectorName) {
        requester.start(collectorName);
        return requester.requestStates();
    }

}
