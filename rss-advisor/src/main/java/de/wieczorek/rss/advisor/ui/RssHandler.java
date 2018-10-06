package de.wieczorek.rss.advisor.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.advisor.persistence.TradingEvaluationResult;
import de.wieczorek.rss.core.ui.Resource;

@Resource
@Path("/")
@ApplicationScoped
public class RssHandler {

    @Inject
    private Controller controller;

    @GET
    @Path("train")
    public void train() {
	controller.trainNeuralNetwork();
    }

    @GET
    @Path("sentiment")
    @Produces(MediaType.APPLICATION_JSON)
    public TradingEvaluationResult predict() {
	return controller.predict();
    }

}
