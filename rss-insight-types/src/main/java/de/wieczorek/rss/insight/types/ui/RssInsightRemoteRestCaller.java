package de.wieczorek.rss.insight.types.ui;

import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class RssInsightRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 11020)
    private WebTarget target;

    public SentimentEvaluationResult now() {
        return target.path("/sentiment/now").request(MediaType.APPLICATION_JSON).get(SentimentEvaluationResult.class);
    }

    public List<SentimentAtTime> all() {
        return target.path("/sentiment/all").request(MediaType.APPLICATION_JSON).get(new GenericType<List<SentimentAtTime>>() {
        });
    }
}
