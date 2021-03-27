package de.wieczorek.rss.trading.ui;

import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.series.SeriesHelper;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.trading.business.Controller;
import de.wieczorek.rss.trading.persistence.LiveAccount;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
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
    public List<LiveAccount> getAccounts(@PathParam("offset") String offset, @QueryParam("maxSize") int maxResultSize) {
        var accountStates = controller.getAccountsAfter(LocalDateTime.now().minus(DateStringParser.parseDuration(offset)));
        if (maxResultSize != 0) {
            accountStates = SeriesHelper.thinOutSeries(accountStates, maxResultSize);
        }
        return accountStates;
    }
}
