package de.wieczorek.rss.trading.ui;

import de.wieczorek.rss.core.ui.Resource;
import de.wieczorek.rss.trading.business.Controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Resource
@Path("/")
@ApplicationScoped
public class RssHandler {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("simulate")
    public void simulate() {
        controller.triggerTrading();
    }
}
