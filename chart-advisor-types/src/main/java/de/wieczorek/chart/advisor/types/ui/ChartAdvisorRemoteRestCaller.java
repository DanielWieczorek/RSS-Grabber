package de.wieczorek.chart.advisor.types.ui;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class ChartAdvisorRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 14020)
    private WebTarget target;


    @GET
    @Path("sentiment")
    @Produces(MediaType.APPLICATION_JSON)
    public TradingEvaluationResult predict() {
        return target.path("/sentiment").request(MediaType.APPLICATION_JSON).get(TradingEvaluationResult.class);
    }

    @GET
    @Path("sentiment/24h")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> predict24h() {
        return target.path("/sentiment/24h").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });

    }

    @GET
    @Path("sentiment/24hAbsolute")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> predict24hAbsolute() {
        return target.path("/sentiment/24hAbsolute").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });
    }

    @GET
    @Path("sentiment/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TradingEvaluationResult> getAllSentiments() {
        return target.path("/sentiment/all").request(MediaType.APPLICATION_JSON).get(new GenericType<List<TradingEvaluationResult>>() {
        });
    }

    @GET
    @Path("recompute")
    public void recompute() {
        target.path("/recompute").request(MediaType.APPLICATION_JSON).get();
    }
}
