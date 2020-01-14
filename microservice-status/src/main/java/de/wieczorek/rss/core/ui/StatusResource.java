package de.wieczorek.rss.core.ui;

import de.wieczorek.rss.core.business.Controller;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.jgroups.MicroserviceDirectory;
import de.wieczorek.rss.core.jgroups.ServiceMetadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;

@Resource
@ApplicationScoped
@Path("/")
public class StatusResource {

    @Context
    private HttpServletRequest request;

    @Inject
    private MicroserviceDirectory directory;

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getstatus")
    public List<ServiceMetadata> status() {
        return controller.status();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("start/{service}")
    public List<ServiceMetadata> start(@PathParam("service") String service) {
        return controller.startService(service);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("stop/{service}")
    public List<ServiceMetadata> stop(@PathParam("service") String service) {
        return controller.stopService(service);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("routing/{service}/{targetUri : .+}")
    public String routingGet(@PathParam("service") String service, @PathParam("targetUri") String targetUri) {
        ServiceMetadata metadata = directory.getMetadataForService(service);

        if (metadata == null) {
            throw new NotFoundException();
        }

        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            map.putSingle(headerName, request.getHeader(headerName));
        }

        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://" + metadata.getBindHostname() + ":" + metadata.getBindPort() + "/" + targetUri)
                .request(request.getHeader("Accept").split(",")).headers(map).get(String.class);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("routing/{service}/{targetUri : .+}")
    public String routingPost(@PathParam("service") String service, @PathParam("targetUri") String targetUri)
            throws IOException {
        ServiceMetadata metadata = directory.getMetadataForService(service);

        if (metadata == null) {
            throw new NotFoundException();
        }

        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            map.putSingle(headerName, request.getHeader(headerName));
        }

        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://" + metadata.getBindHostname() + ":" + metadata.getBindPort() + "/" + targetUri)

                .request(request.getHeader("Accept").split(",")).headers(map)
                .post(Entity.json(new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
                        .reduce(String::concat).get()), String.class);
    }
}
