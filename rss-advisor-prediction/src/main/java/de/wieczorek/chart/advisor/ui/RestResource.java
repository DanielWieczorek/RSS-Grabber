package de.wieczorek.chart.advisor.ui;

import de.wieczorek.chart.advisor.business.Controller;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.advisor.types.ui.CallableResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public TradingEvaluationResult predict() {
        return controller.predict();
    }

    @GET
    @Path("24h")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> predict24h() {
        return controller.get24hPrediction();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> getAllSentiments() {
        return controller.getAllPredictions();
    }
}
