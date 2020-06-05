package de.wieczorek.chart.advisor.ui;

import de.wieczorek.chart.advisor.business.Controller;
import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.advisor.types.ui.CallableResource;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Resource
@Path("/")
@ApplicationScoped
@EntityManagerContext
public class RestResource implements CallableResource {

    @Inject
    private Controller controller;

    @GET
    @Path("sentiment")
    @Produces(MediaType.APPLICATION_JSON)
    public TradingEvaluationResult predict() {
        return controller.predict();
    }

    @GET
    @Path("sentiment/24h")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> predict24h() {
        return controller.get24hPrediction();
    }

    @GET
    @Path("sentiment/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> getAllSentiments() {
        return controller.getAllPredictions();
    }

    @GET
    @Path("recompute")
    public void recompute() {
        controller.recompute();
    }

}
