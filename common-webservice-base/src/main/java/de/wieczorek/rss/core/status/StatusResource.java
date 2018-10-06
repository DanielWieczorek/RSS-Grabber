package de.wieczorek.rss.core.status;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.core.jgroups.CollectorStatus;
import de.wieczorek.rss.core.ui.Resource;

@Resource
@Path("status/")
@ApplicationScoped
public class StatusResource {

    @Inject
    private ServiceStatusHolder holder;

    @Inject
    private Event<StatusUpdate> updateEvent;

    @GET
    @Path("start")
    public void start() {
	StatusUpdate update = new StatusUpdate();
	update.setStatus(ServiceState.STARTED);
	updateEvent.fire(update);
    }

    @GET
    @Path("stop")
    public void stop() {
	StatusUpdate update = new StatusUpdate();
	update.setStatus(ServiceState.STOPPED);
	updateEvent.fire(update);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CollectorStatus status() {
	CollectorStatus status = new CollectorStatus();
	status.setStatus(holder.getStatus() == ServiceState.STARTED ? "running" : "stopped");
	return status;
    }
}
