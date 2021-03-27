package de.wieczorek.chart.core.ui;

import de.wieczorek.chart.core.business.Controller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.CallableResource;
import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Resource
@ApplicationScoped
@EntityManagerContext
@Path("metric")
public class MetricsResource implements CallableResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/between/{start}/{end}")
    public List<ChartMetricRecord> metricBetween(@PathParam("start") long start,
                                                 @PathParam("end") long end) {
        var startDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(start), ZoneId.of("UTC"));
        var endDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(end), ZoneId.of("UTC"));
        return controller.getBetween(startDate, endDate);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{offset}")
    public List<ChartMetricRecord> metric(@PathParam("offset") String offset) {
        return controller.getAfter(LocalDateTime.now().minus(DateStringParser.parseDuration(offset)));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/now")
    public List<ChartMetricRecord> metricNow() {
        return controller.getNow();
    }

}
