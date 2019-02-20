package de.wieczorek.rss.core.jgroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jgroups.Address;

@ApplicationScoped
public class MicroserviceDirectory {
    private Map<String, ServiceMetadata> statusInfo = new HashMap<>();
    private Map<Address, String> addressToServiceNameMapping = new HashMap<>();

    protected void receiveStatusInfo(@Observes StatusMessage message) {
	StatusResponse status = message.getResponse();
	if (status.getBindHostname() != null) {
	    System.out.println("received info from " + status.getCollectorName() + " at " + status.getBindHostname()
		    + ":" + status.getBindPort());

	    ServiceMetadata metadata = createServiceMetadata(status);

	    statusInfo.put(status.getCollectorName(), metadata);
	    addressToServiceNameMapping.put(message.getAddress(), status.getCollectorName());
	}
    }

    private ServiceMetadata createServiceMetadata(StatusResponse status) {
	ServiceMetadata metadata = new ServiceMetadata();
	metadata.setBindHostname(status.getBindHostname());
	metadata.setBindPort(status.getBindPort());
	metadata.setName(status.getCollectorName());
	return metadata;
    }

    protected void removeMembers(@Observes List<Address> leftMembers) {
	leftMembers.forEach(member -> {
	    statusInfo.remove(addressToServiceNameMapping.get(member));
	    addressToServiceNameMapping.remove(member);
	});
    }

    public List<ServiceMetadata> getMetadata() {
	return new ArrayList<>(statusInfo.values());

    }

    public ServiceMetadata getMetadataForService(String service) {
	return statusInfo.get(service);
    }

}
