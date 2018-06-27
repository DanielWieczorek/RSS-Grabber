package de.wieczorek.rss.core.business;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.persistence.RssEntryDao;

@ApplicationScoped
public class RssReaderProducer {
    @Inject
    private RssEntryDao dao;

    @Inject
    @Any
    private Instance<RssConfig> configurations;

    @Produces
    public List<RssReader> buildRssReaders() {
	List<RssReader> result = new ArrayList<>();
	for (RssConfig config : configurations) {
	    System.out.println("Building reader for " + config.getServiceName());
	    result.add(new RssReader(config, dao));
	}

	return result;
    }

}
