package de.wieczorek.rss.trading.common;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;

@ApplicationScoped
public class DataLoader {

    public List<ChartEntry> loadChartEntries24h() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});
    }

    public List<TradingEvaluationResult> loadSentiments24h() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12020/sentiment/24h").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<TradingEvaluationResult>>() {
		});
    }

    public List<ChartEntry> loadAllChartEntries() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});
    }

    public List<TradingEvaluationResult> loadAllSentiments() {
	return ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://localhost:12020/sentiment/all").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<TradingEvaluationResult>>() {
		});
    }
}
