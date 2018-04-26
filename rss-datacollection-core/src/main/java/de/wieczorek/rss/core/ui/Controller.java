package de.wieczorek.rss.core.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.business.RssReader;
import de.wieczorek.rss.core.jgroups.RestInfoSender;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());
    // private boolean isStarted;

    @Inject
    private RssReader reader;

    public void start() {
	logger.info("started");
	reader.start();
    }

    public void stop() {
	logger.info("stopped");
	reader.stop();
    }

}
