package de.wieczorek.rss.bitcoinmagazine;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.timer.RecurrentTask;

@ApplicationScoped
@RecurrentTask(interval = 10, unit = TimeUnit.MINUTES)
public class RssReaderConfigurationProvider extends RssReader {

    public class RssReaderConfiguration extends RssConfig {
	public RssReaderConfiguration() {
	    serviceName = "rss-collector-bitcoinmagazine";
	    feedUrl = "https://bitcoinmagazine.com/feed/";
	    filter = new BitcoinmagazineMessageFilter();
	    transformer = new BitcoinmagazineMessageTransformer();

	}
    }

    @Override
    protected RssConfig getRssConfig() {
	return new RssReaderConfiguration();
    }
}
