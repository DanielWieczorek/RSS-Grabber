package de.wieczorek.chart.advisor.types.ui;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class ChartAdvisorRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 14020)
    private WebTarget target;

    public TradingEvaluationResult predictNow() {
        return target.path("/sentiment/now").request(MediaType.APPLICATION_JSON).get(TradingEvaluationResult.class);
    }

    public List<TradingEvaluationResult> predict24h() {
        return target.path("/sentiment/24h").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });
    }

    public List<TradingEvaluationResult> predict(String offset) {
        return target.path("/sentiment/" + offset).request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });

    }

    public List<TradingEvaluationResult> getAllSentiments() {
        return target.path("/sentiment/all").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });
    }
}
