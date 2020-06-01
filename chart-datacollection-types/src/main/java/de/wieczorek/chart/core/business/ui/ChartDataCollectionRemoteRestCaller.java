package de.wieczorek.chart.core.business.ui;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class ChartDataCollectionRemoteRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.REMOTE, port = 12000)
    private WebTarget target;


    @Override
    public List<ChartEntry> ohlcv() {
        return target.path("/ohlcv/").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ChartEntry>>() {
        });
    }

    @Override
    public List<ChartEntry> ohlcv24h() {
        return target.path("/ohlcv/24h").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ChartEntry>>() {
        });
    }

    @Override
    public List<ChartEntry> ohlcv24d() {
        return target.path("/ohlcv/24d").request(MediaType.APPLICATION_JSON).get(new GenericType<List<ChartEntry>>() {
        });
    }
}
