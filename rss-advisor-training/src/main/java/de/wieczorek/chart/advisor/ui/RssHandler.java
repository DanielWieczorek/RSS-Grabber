package de.wieczorek.chart.advisor.ui;

import de.wieczorek.rss.core.persistence.EntityManagerContext;
import de.wieczorek.rss.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Resource
@EntityManagerContext
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
