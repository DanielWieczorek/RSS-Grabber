package de.wieczorek.rss.insight.ui;

import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.insight.business.Controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
}
