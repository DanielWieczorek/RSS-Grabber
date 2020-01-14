package de.wieczorek.rss.core.jgroups;

import de.wieczorek.rss.core.feature.FeatureDescriptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import java.util.List;

@ApplicationScoped
public class StatusRequester {

    @Inject
    private MicroserviceDirectory directory;


    public List<ServiceMetadata> requestStates() {

        List<ServiceMetadata> metadata = directory.getMetadata();
        directory.getMetadata().parallelStream().forEach(md -> {
            try {
                md.setStatus(ClientBuilder.newClient().target("http://" + md.getBindHostname() + ":" + md.getBindPort())
                        .path("/status").request().accept("application/json").get(MicroserviceStatus.class).getStatus());

            } catch (Exception e) {
                md.setStatus("failed");
                e.printStackTrace();
            }

            try {
                md.setFeatures(ClientBuilder.newClient()
                        .target("http://" + md.getBindHostname() + ":" + md.getBindPort()).path("/feature/info")
                        .request().accept("application/json").get(new GenericType<List<FeatureDescriptor>>() {
                        }));
            } catch (Exception e) {
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
