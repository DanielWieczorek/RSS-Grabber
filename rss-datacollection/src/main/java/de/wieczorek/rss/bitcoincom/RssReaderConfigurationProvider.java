package de.wieczorek.rss.bitcoincom;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;

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
            serviceName = "rss-collector-bitcoincom";
            feedUrl = "https://news.bitcoin.com/feed/";
            filter = new NoOperationMessageFilter();
            transformer = new BitcoincomMessageTransformer();

        }
    }
}
