package de.wieczorek.rss.core.ui;

import de.wieczorek.core.ui.Resource;
import de.wieczorek.rss.classification.types.ClassificationStatistics;
import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.classification.types.ui.CallableResource;
import de.wieczorek.rss.core.business.Controller;
import de.wieczorek.core.persistence.EntityManagerContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@Resource
@EntityManagerContext
@ApplicationScoped
public class RssHandler implements CallableResource {
    @Inject
    private Controller controller;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("find")
    public List<RssEntry> find() {
        return controller.readUnclassifiedEntries();

    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("classified")
    public List<RssEntry> classified() {
        return controller.readClassfiedEntries();
    }

    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("classify")
    public void classify(RssEntry classifiedEntry) {
        controller.updateClassification(classifiedEntry);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("statistics")
    public ClassificationStatistics statistics() {
        return controller.getClassificationStatistics();
    }

}
