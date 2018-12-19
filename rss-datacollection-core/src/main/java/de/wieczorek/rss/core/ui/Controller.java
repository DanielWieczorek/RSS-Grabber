package de.wieczorek.rss.core.ui;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.persistence.RssEntryDao;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.types.RssEntry;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
	start();
    }

    @Inject
    private RssEntryDao dao;

    @Inject
    private RecurrentTaskManager taskManager;

    @Override
    public void start() {
	logger.info("started");
	taskManager.start();
    }

    @Override
    public void stop() {
	logger.info("stopped");
	taskManager.stop();
    }

    public List<RssEntry> readEntriesAfter(Date before) {
	logger.info("get all unclassified");
	return dao.findAllAfter(before);
    }

    public List<RssEntry> readEntries24h() {
	return dao.findAll24h();
    }

    public List<RssEntry> readAllEntries() {
	return dao.findAll();
    }
}
