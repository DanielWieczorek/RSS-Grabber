package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.nn.IDataGenerator;
import de.wieczorek.rss.advisor.types.NetInputItem;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.insight.types.SentimentAtTime;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class DataGenerator implements IDataGenerator<NetInputItem> {
    @Override
    public List<NetInputItem> generate() {
        List<SentimentAtTime> sentiments = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:11020/sentiment-at-time").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<SentimentAtTime>>() {
                });

        List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartEntry>>() {
                });

        return new DataPreparator().withChartData(chartEntries).withSentiments(sentiments).getData();
    }
}
