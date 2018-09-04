package de.wieczorek.rss.cryptonewsnet;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.NoOperationMessageFilter;
import de.wieczorek.rss.core.business.NoOperationMessageTransformer;
import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.timer.RecurrentTask;

@ApplicationScoped
@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
public class RssReaderConfigurationProvider extends RssReader {

    public class RssReaderConfiguration extends RssConfig {
	public RssReaderConfiguration() {
	    serviceName = "rss-collector-cryptonewsnet";
	    feedUrl = "https://www.crypto-news.net/feed/";
	    filter = new NoOperationMessageFilter();
	    transformer = new NoOperationMessageTransformer();

	}
    }

    @Override
    protected RssConfig getRssConfig() {
	return new RssReaderConfiguration();
    }
}
