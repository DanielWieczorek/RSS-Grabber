package de.wieczorek.rss.trading.common;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class DataLoader {

    public List<ChartEntry> loadChartEntries24h() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartEntry>>() {
                });
    }

    public List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> loadSentiments24h() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:12020/sentiment/24h").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<de.wieczorek.rss.advisor.types.TradingEvaluationResult>>() {
                });
    }

    public List<ChartMetricRecord> loadMetrics24h() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:13000/metric/24h").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartMetricRecord>>() {
                });
    }

    public List<ChartEntry> loadAllChartEntries() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartEntry>>() {
                });
    }

    public List<ChartMetricRecord> loadAllMetrics() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:13000/metric/all").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartMetricRecord>>() {
                });
    }

    public List<de.wieczorek.rss.advisor.types.TradingEvaluationResult> loadAllSentiments() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:12020/sentiment/all").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<de.wieczorek.rss.advisor.types.TradingEvaluationResult>>() {
                });
    }

    public List<TradingEvaluationResult> loadAllMetricSentiments() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:14020/sentiment/all").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<TradingEvaluationResult>>() {
                });
    }

    public List<TradingEvaluationResult> loadMetricSentiments24h() {
        return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:14020/sentiment/24h").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<TradingEvaluationResult>>() {
                });
    }
}
