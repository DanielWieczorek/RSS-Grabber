package de.wieczorek.rss.newsbtc;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.timer.RecurrentTask;

@ApplicationScoped
@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
public class RssReaderConfigurationProvider extends RssReader {

    public class RssReaderConfiguration extends RssConfig {
        public RssReaderConfiguration() {
            serviceName = "rss-collector-newsbtc";
            feedUrl = "https://www.newsbtc.com/feed/";
            filter = new NoOperationMessageFilter();
            transformer = new NewsbtcMessageTransformer();

        }
    }

    @Override
    protected RssConfig getRssConfig() {
        return new RssReaderConfiguration();
    }
}
