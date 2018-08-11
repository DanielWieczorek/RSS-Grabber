package de.wieczorek.rss.insight.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.wieczorek.rss.core.ui.Resource;
import de.wieczorek.rss.insight.business.SentimentEvaluationResult;

@Resource
@Path("/")
@ApplicationScoped
public class RssHandlerProducer {

    @Inject
    private Controller controller;

    @GET
    @Path("train")
    public void train() {
	controller.trainNeuralNetwork();
    }

    @GET
    @Path("sentiment")
    public SentimentEvaluationResult sentiment() {
	return controller.predict();
    }

}
