package de.wieczorek.rss.core.jgroups;

import org.jgroups.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class MicroserviceDirectory {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceDirectory.class);

    private Map<String, ServiceMetadata> statusInfo = new ConcurrentHashMap<>();
    private Map<Address, String> addressToServiceNameMapping = new ConcurrentHashMap<>();

    protected void receiveStatusInfo(@Observes StatusMessage message) {
        StatusResponse status = message.getResponse();
        if (status.getBindHostname() != null) {
            logger.debug("adding member " + status.getCollectorName() + " at " + status.getBindHostname()
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
            logger.debug("removing member " + member.toString());

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
