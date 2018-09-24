package de.wieczorek.rss.core.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.jgroups.CollectorStatus;

@Path("/")
@Resource
@ApplicationScoped
public class RssHandler {
    @Inject
    private Controller controller;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("find")
    public List<RssEntry> find() {
	return controller.readUnclassifiedEntries();

    }

    @GET
    @Path("start")
    public void start() {
	controller.start();
    }

    @GET
    @Path("stop")
    public void stop() {
	controller.stop();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("status")
    public CollectorStatus status() {
	CollectorStatus status = new CollectorStatus();
	status.setStatus(controller.isStarted() ? "running" : "stopped");
	return status;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("classified")
    public List<RssEntry> classified() {
	return controller.readClassfiedEntries();
    }

    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("classify")
    public void classify(RssEntry classifiedEntry) {
	controller.updateClassification(classifiedEntry);
    }

}
