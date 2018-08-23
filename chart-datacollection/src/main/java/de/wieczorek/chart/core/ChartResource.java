package de.wieczorek.chart.core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.ui.Controller;
import de.wieczorek.rss.core.ui.Resource;

@Resource
@ApplicationScoped
@Path("/")
public class ChartResource {

    @Inject
    private Controller controller;

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
    public void status() {
	controller.stop();
    }

}
