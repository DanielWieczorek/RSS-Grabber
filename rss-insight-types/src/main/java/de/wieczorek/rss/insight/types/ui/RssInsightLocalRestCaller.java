package de.wieczorek.rss.insight.types.ui;

import de.wieczorek.rss.core.ui.Target;
import de.wieczorek.rss.core.ui.TargetType;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.SentimentEvaluationResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class RssInsightLocalRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.LOCAL, port = 11020)
    private WebTarget target;

    public SentimentEvaluationResult sentiment() {
        return target.path("/sentiment").request(MediaType.APPLICATION_JSON).get(SentimentEvaluationResult.class);
    }

    public List<SentimentAtTime> allSentiments() {
        return target.path("/sentiment-at-time").request(MediaType.APPLICATION_JSON).get(new GenericType<List<SentimentAtTime>>() {
        });
    }

    public void recalculate() {
        target.path("/recompute").request(MediaType.APPLICATION_JSON).get();
    }
}
