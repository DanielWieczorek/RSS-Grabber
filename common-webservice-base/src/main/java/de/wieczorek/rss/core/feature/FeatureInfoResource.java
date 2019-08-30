package de.wieczorek.rss.core.feature;

import de.wieczorek.rss.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Resource
@ApplicationScoped
@Path("feature/info")
public class FeatureInfoResource {

    @Inject
    private FeatureDescriptorHolder holder;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public List<FeatureDescriptor> getFeatures() {
        return holder.getFeatures();
    }

}
