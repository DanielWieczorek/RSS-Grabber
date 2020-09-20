package de.wieczorek.rss.insight.ui;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.insight.business.Controller;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;
import de.wieczorek.rss.insight.types.ui.CallableResource;

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
    public SentimentEvaluationResult now() {
        return controller.predict();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SentimentAtTime> all() {
        return controller.getAllSentimentAtTime();
    }
}
