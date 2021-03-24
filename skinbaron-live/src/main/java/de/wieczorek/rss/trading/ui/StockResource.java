package de.wieczorek.rss.trading.ui;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.trading.business.Controller;
import de.wieczorek.rss.trading.db.Stock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Resource
@ApplicationScoped
@EntityManagerContext
@Path("/stock")
public class StockResource {

    @Inject
    private Controller controller;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public List<Stock> getTotalStock() {
        return controller.getStock();
    }
}
