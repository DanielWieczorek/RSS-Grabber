package de.wieczorek.rss.classification.types.ui;

import de.wieczorek.rss.classification.types.ClassificationStatistics;
import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.core.ui.Target;
import de.wieczorek.rss.core.ui.TargetType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class RssAdvisorRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 10020)
    private WebTarget target;


    public List<RssEntry> find() {
        return target.path("/find").request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
        });
    }

    public List<RssEntry> classified() {
        return target.path("/classified").request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
        });
    }

    public ClassificationStatistics statistics() {
        return target.path("/statistics").request(MediaType.APPLICATION_JSON).get(ClassificationStatistics.class);
    }
}
