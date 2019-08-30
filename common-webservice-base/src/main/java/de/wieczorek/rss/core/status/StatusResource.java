package de.wieczorek.rss.core.status;

import de.wieczorek.rss.core.jgroups.CollectorStatus;
import de.wieczorek.rss.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
        updateEvent.fire(StatusUpdate.started());
    }

    @GET
    @Path("stop")
    public void stop() {
        updateEvent.fire(StatusUpdate.stopped());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CollectorStatus status() {
        CollectorStatus status = new CollectorStatus();
        status.setStatus(holder.getStatus() == ServiceState.STARTED ? "running" : "stopped");
        return status;
    }
}
