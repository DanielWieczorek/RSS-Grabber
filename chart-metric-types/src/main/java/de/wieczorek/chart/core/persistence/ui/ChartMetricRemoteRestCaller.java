package de.wieczorek.chart.core.persistence.ui;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class ChartMetricRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 13000)
    private WebTarget target;

    public List<ChartMetricRecord> metricAll() {
        return target.path("/metric/all").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ChartMetricRecord>>() {
        });
    }

    public List<ChartMetricRecord> metric24h() {
        return target.path("/metric/24h").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ChartMetricRecord>>() {
        });
    }

    public List<ChartMetricRecord> metricNow() {
        return target.path("/metric/now").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ChartMetricRecord>>() {
        });
    }

}
