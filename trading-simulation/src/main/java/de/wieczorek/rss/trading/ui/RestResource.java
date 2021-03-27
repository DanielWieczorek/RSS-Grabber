package de.wieczorek.rss.trading.ui;

import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.trading.business.Controller;
import de.wieczorek.rss.trading.common.trading.Trade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Resource
@Path("/simulate")
@ApplicationScoped
public class RestResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{offset}")
    public List<Trade> simulate(@PathParam("offset") String offset) {
        DateStringParser.parseDuration(offset); // validation
        return controller.simulate(offset);
    }
}
