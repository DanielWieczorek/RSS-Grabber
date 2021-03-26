package de.wieczorek.rss.trading.ui;

import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.trading.business.Controller;
import de.wieczorek.rss.trading.persistence.PerformedTrade;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;

@Resource
@Path("trading")
@ApplicationScoped
@EntityManagerContext
public class TradingResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{offset}")
    public List<PerformedTrade> getTrades(@PathParam("offset") String offset) {
        return controller.getTradesAfter(LocalDateTime.now().minus(DateStringParser.parseDuration(offset)));
    }

    @GET
    @Path("configuration/reload")
    public void reloadConfiguration() {
        controller.replaceOracle();
    }
}
