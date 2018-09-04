package de.wieczorek.rss.core.ui;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.business.RssEntry;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.persistence.RssEntryDao;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;

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
    private RecurrentTaskManager taskManager;

    public void start() {
	logger.info("started");
	taskManager.start();
	isStarted = true;
    }

    public void stop() {
	logger.info("stopped");
	taskManager.stop();
	isStarted = false;
    }

    public boolean isStarted() {
	return isStarted;
    }

    public List<RssEntry> readEntriesAfter(Date before) {
	logger.info("get all unclassified");
	return dao.findAllAfter(before);
    }

    public List<RssEntry> readEntries24h() {
	return dao.findAll24h();
    }

}
