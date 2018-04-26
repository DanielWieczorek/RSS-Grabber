package de.wieczorek.rss.core.jgroups;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;

@ApplicationScoped
public class StatusRequester {

    @Inject
    private CollectorsDirectory directory;

    public List<ServiceMetadata> requestStates() {

	List<ServiceMetadata> metadata = directory.getMetadata();
	directory.getMetadata().stream().forEach(md -> {
	    try {
		md.setStatus(ClientBuilder.newClient().target("http://" + md.getBindHostname() + ":" + md.getBindPort())
			.path("/status").request().accept("application/json").get(CollectorStatus.class).getStatus());
	    } catch (WebApplicationException | ProcessingException e) {
		md.setStatus("failed");
		e.printStackTrace();
	    }
	});

	return metadata;
    }
}
