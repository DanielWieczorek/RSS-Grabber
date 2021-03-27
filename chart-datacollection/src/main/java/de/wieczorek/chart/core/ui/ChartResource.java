package de.wieczorek.chart.core.ui;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.Controller;
import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.series.SeriesHelper;
import de.wieczorek.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
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
    @Path("all")
    public List<ChartEntry> ohlcv() {
        return controller.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{offset}")
    public List<ChartEntry> ohlcvOffset(@PathParam("offset") String offset, @QueryParam("maxSize") int maxResultSize) {
        var chartEntries = controller.getStartingFrom(LocalDateTime.now().minus(DateStringParser.parseDuration(offset)));
        if (maxResultSize != 0) {
            chartEntries = SeriesHelper.thinOutSeries(chartEntries, maxResultSize);
        }
        return chartEntries;
    }

}
