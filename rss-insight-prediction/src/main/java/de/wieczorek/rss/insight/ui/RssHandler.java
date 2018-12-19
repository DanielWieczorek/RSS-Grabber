package de.wieczorek.rss.insight.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.wieczorek.rss.core.ui.Resource;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

@Resource
@Path("/")
@ApplicationScoped
public class RssHandler {

    @Inject
    private Controller controller;

    @GET
    @Path("sentiment")
    @Produces(MediaType.APPLICATION_JSON)
    public SentimentEvaluationResult sentiment() {
	return controller.predict();
    }

    @GET
    @Path("sentiment-at-time")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SentimentAtTime> allSentiments() {
	return controller.getAllSentimentAtTime();
    }

    @GET
    @Path("recompute")
    @Produces(MediaType.APPLICATION_JSON)
    public void recalculate() {
	controller.recalculate();
    }

}
