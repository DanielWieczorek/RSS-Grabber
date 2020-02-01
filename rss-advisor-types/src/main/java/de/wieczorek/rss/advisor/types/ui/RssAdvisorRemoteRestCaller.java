package de.wieczorek.rss.advisor.types.ui;

import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class RssAdvisorRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 12020)
    private WebTarget target;

    public TradingEvaluationResult predict() {
        return target.path("/sentiment").request(MediaType.APPLICATION_JSON).get(TradingEvaluationResult.class);
    }

    public List<TradingEvaluationResult> predict24h() {
        return target.path("/sentiment/24h").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });
    }

    public List<TradingEvaluationResult> getAllSentiments() {
        return target.path("/sentiment/all").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });
    }

    public void recompute() {
        target.path("/metric/recompute").request(MediaType.APPLICATION_JSON).get();
    }
}
