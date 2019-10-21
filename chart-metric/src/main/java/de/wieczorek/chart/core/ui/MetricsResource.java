package de.wieczorek.chart.core.ui;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
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
@Path("metric")
public class MetricsResource {

    @Inject
    private Controller controller;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public List<ChartMetricRecord> metricAll() {
        return controller.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/24h")
    public List<ChartMetricRecord> metric24h() {
        return controller.get24h();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/now")
    public List<ChartMetricRecord> metricNow() {
        return controller.getNow();
    }

    @GET
    @Path("recompute")
    public void recompute() {
        controller.recompute();
    }
}
