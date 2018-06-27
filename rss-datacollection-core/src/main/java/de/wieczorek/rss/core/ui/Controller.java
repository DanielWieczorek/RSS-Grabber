package de.wieczorek.rss.core.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.jgroups.RestInfoSender;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());
    private boolean isStarted;

    @Inject
    private List<RssReader> readers;

    public void start() {

	logger.info("started");
	readers.forEach(RssReader::start);
	isStarted = true;
    }

    public void stop() {
	logger.info("stopped");
	readers.forEach(RssReader::stop);
	isStarted = false;
    }

    public boolean isStarted() {
	return isStarted;
    }

}
