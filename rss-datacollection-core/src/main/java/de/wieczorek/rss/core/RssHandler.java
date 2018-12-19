package de.wieczorek.rss.core;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.core.ui.Controller;
import de.wieczorek.rss.core.ui.Resource;
import de.wieczorek.rss.types.RssEntry;

@Resource
@ApplicationScoped
@Path("/")
public class RssHandler {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rss-entries/24h")
    public List<RssEntry> entriesFromLast24h() {
	return controller.readEntries24h();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("rss-entries/{unixTimestamp}")
    public List<RssEntry> entriesAfter(@PathParam("unixTimestamp") long unixTimestamp) {
	return controller.readEntriesAfter(new Date(unixTimestamp));

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("rss-entries")
    public List<RssEntry> allEntries() {
	return controller.readAllEntries();

    }
}
