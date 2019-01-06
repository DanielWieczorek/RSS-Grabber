package de.wieczorek.rss.core.jgroups;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class StatusRequester {

    @Inject
    private CollectorsDirectory directory;

    public List<ServiceMetadata> requestStates() {

	List<ServiceMetadata> metadata = directory.getMetadata();
	directory.getMetadata().parallelStream().forEach(md -> {
	    try {
		md.setStatus(ClientBuilder.newClient().target("http://" + md.getBindHostname() + ":" + md.getBindPort())
			.path("/status").request().accept("application/json").get(CollectorStatus.class).getStatus());
	    } catch (Exception e) {
		md.setStatus("failed");
		e.printStackTrace();
	    }
	});

	return metadata;
    }

    public void stop(String collectorName) {
	ServiceMetadata metadata = directory.getMetadataForService(collectorName);

	ClientBuilder.newClient().target("http://" + metadata.getBindHostname() + ":" + metadata.getBindPort())
		.path("/status/stop").request().accept("application/json").get();

    }

    public void start(String collectorName) {
	ServiceMetadata metadata = directory.getMetadataForService(collectorName);

	ClientBuilder.newClient().target("http://" + metadata.getBindHostname() + ":" + metadata.getBindPort())
		.path("/status/start").request().accept("application/json").get();

    }
}
