package de.wieczorek.rss.core.jgroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class CollectorsDirectory {
    private Map<String, ServiceMetadata> statusInfo;

    public CollectorsDirectory() {
	statusInfo = new HashMap<>();
    }

    private void receiveStatusInfo(@Observes StatusResponse status) {
	if (status.getBindHostname() != null) {
	    System.out.println("received info from " + status.getCollectorName() + " at " + status.getBindHostname()
		    + ":" + status.getBindPort());

	    ServiceMetadata metadata = new ServiceMetadata();
	    metadata.setBindHostname(status.getBindHostname());
	    metadata.setBindPort(status.getBindPort());
	    metadata.setCollectorName(status.getCollectorName());

	    statusInfo.put(status.getCollectorName(), metadata);
	}
    }

    public List<ServiceMetadata> getMetadata() {
	return new ArrayList<>(statusInfo.values());

    }

    public ServiceMetadata getMetadataForService(String service) {
	return statusInfo.get(service);
    }

}
