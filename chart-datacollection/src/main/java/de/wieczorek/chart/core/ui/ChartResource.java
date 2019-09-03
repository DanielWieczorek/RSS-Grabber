package de.wieczorek.chart.core.ui;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.ui.Controller;
import de.wieczorek.rss.core.persistence.EntityManagerContext;
import de.wieczorek.rss.core.ui.Resource;

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
@Path("ohlcv")
public class ChartResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public List<ChartEntry> ohlcv() {
        return controller.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("24h")
    public List<ChartEntry> ohlcv24h() {
        return controller.get24h();
    }

}
