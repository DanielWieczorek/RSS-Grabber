package de.wieczorek.chart.core;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.ui.Controller;
import de.wieczorek.rss.core.jgroups.CollectorStatus;
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
    public CollectorStatus status() {
	CollectorStatus status = new CollectorStatus();
	status.setStatus(controller.isStarted() ? "running" : "stopped");
	return status;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ohlcv")
    public List<ChartEntry> ohlcv() {
	return controller.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ohlcv/24h")
    public List<ChartEntry> ohlcv24h() {
	return controller.get24h();
    }

}
