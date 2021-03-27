package de.wieczorek.chart.advisor.ui;

import de.wieczorek.chart.advisor.business.Controller;
import de.wieczorek.core.date.DateStringParser;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.advisor.types.ui.CallableResource;

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
@EntityManagerContext
@Path("sentiment")
@ApplicationScoped
public class RestResource implements CallableResource {

    @Inject
    private Controller controller;

    @GET
    @Path("now")
    @Produces(MediaType.APPLICATION_JSON)
    public TradingEvaluationResult predictNow() {
        return controller.predict();
    }

    @GET
    @Path("{offset}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> predict(@PathParam("offset") String offset) {
        return controller.getPrediction(LocalDateTime.now().minus(DateStringParser.parseDuration(offset)));
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> getAllSentiments() {
        return controller.getAllPredictions();
    }
}
