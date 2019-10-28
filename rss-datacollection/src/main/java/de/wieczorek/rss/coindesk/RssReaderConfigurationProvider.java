package de.wieczorek.rss.coindesk;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.NoOperationMessageTransformer;
import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.persistence.EntityManagerContext;
import de.wieczorek.rss.core.timer.RecurrentTask;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@EntityManagerContext
@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
public class RssReaderConfigurationProvider extends RssReader {

    @Override
    protected RssConfig getRssConfig() {
        return new RssReaderConfiguration();
    }

    public static class RssReaderConfiguration extends RssConfig {
        public RssReaderConfiguration() {
            serviceName = "rss-collector-coindesk";
            feedUrl = "http://feeds.feedburner.com/CoinDesk";
            filter = new NoOperationMessageFilter();
            transformer = new NoOperationMessageTransformer();

        }
    }
}
