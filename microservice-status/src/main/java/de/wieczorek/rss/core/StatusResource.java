package de.wieczorek.rss.core;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import de.wieczorek.rss.core.jgroups.ServiceMetadata;
import de.wieczorek.rss.core.ui.Resource;

@Resource
@ApplicationScoped
@Path("/")
public class StatusResource {

    @Inject
    private Controller controller;

    @GET
    @Path("getstatus")
    public List<ServiceMetadata> status() {
	return controller.status();
    }

    @POST
    @Path("start/{service}")
    public List<ServiceMetadata> start(@PathParam("service") String service) {
	return controller.startService(service);
    }

    @POST
    @Path("stop/{service}")
    public List<ServiceMetadata> stop(@PathParam("service") String service) {
	return controller.stopService(service);

    }
}
