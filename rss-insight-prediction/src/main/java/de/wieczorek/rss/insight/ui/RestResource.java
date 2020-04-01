package de.wieczorek.rss.insight.ui;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.insight.business.Controller;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Resource
@EntityManagerContext
@Path("/")
@ApplicationScoped
public class RestResource {

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
