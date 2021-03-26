package de.wieczorek.rss.trading.ui;

import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.trading.business.Controller;
import de.wieczorek.rss.trading.persistence.LiveAccount;

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
@Path("account")
@ApplicationScoped
@EntityManagerContext
public class AccountResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{offset}")
    public List<LiveAccount> get24h(@PathParam("offset") String offset) {
        return controller.getAccountsAfter(LocalDateTime.now().minus(DateStringParser.parseDuration(offset)));

    }
}
