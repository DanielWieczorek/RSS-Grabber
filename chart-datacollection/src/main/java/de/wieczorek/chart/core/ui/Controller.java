package de.wieczorek.chart.core.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.business.ChartReader;
import de.wieczorek.chart.core.persistence.RssEntryDao;
import de.wieczorek.rss.core.jgroups.RestInfoSender;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());
    private boolean isStarted;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
	start();
    }

    @Inject
    private RssEntryDao dao;

    @Inject
    private ChartReader reader;

    public void start() {
	logger.info("started");
	reader.start();
	isStarted = true;
    }

    public void stop() {
	logger.info("stopped");
	reader.stop();
	isStarted = false;
    }

    public boolean isStarted() {
	return isStarted;
    }

}
