package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionRemoteRestCaller;
import de.wieczorek.nn.IDataGenerator;
import de.wieczorek.rss.advisor.types.NetInputItem;
import de.wieczorek.rss.insight.types.SentimentAtTime;
import de.wieczorek.rss.insight.types.ui.RssInsightRemoteRestCaller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DataGenerator implements IDataGenerator<NetInputItem> {

    @Inject
    private RssInsightRemoteRestCaller rssInsightCaller;

    @Inject
    private ChartDataCollectionRemoteRestCaller chartDataCollectionCaller;

    @Override
    public List<NetInputItem> generate() {
        List<SentimentAtTime> sentiments = rssInsightCaller.allSentiments();

        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv();

        return new DataPreparator().withChartData(chartEntries).withSentiments(sentiments).getData();
    }
}
