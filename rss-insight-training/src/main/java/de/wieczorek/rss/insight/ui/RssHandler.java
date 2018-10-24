package de.wieczorek.rss.insight.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
}
