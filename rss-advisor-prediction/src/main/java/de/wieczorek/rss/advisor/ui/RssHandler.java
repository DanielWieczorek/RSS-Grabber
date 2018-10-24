package de.wieczorek.rss.advisor.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.ui.Resource;

@Resource
@Path("/")
@ApplicationScoped
public class RssHandler {

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

}
