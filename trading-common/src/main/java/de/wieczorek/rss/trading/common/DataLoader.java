package de.wieczorek.rss.trading.common;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;

@ApplicationScoped
public class DataLoader {

    public List<ChartEntry> loadChartEntries24h() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});
    }

    public List<TradingEvaluationResult> loadSentiments24h() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:12020/sentiment/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<TradingEvaluationResult>>() {
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

    public List<TradingEvaluationResult> loadAllSentiments() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:12020/sentiment/all").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<TradingEvaluationResult>>() {
		});
    }
}
