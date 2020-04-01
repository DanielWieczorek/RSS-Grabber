package de.wieczorek.rss.core.ui;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.core.business.Controller;
import de.wieczorek.rss.types.RssEntry;
import de.wieczorek.rss.types.ui.CallableResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Resource
@ApplicationScoped
@EntityManagerContext
@Path("rss-entries")
public class RestResource implements CallableResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("24h")
    public List<RssEntry> entriesFromLast24h() {
        return controller.readEntries24h();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("{unixTimestamp}")
    public List<RssEntry> entriesAfter(@PathParam("unixTimestamp") long unixTimestamp) {
        return controller.readEntriesAfter(new Date(unixTimestamp));

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/")
    public List<RssEntry> allEntries() {
        return controller.readAllEntries();
    }
}
