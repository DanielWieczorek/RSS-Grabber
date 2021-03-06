package de.wieczorek.rss.core.business;

import de.wieczorek.rss.core.persistence.RssEntryDao;
import de.wieczorek.core.timer.RecurrentTaskManager;
import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.types.RssEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Inject
    private RssEntryDao dao;
    @Inject
    private RecurrentTaskManager taskManager;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        start();
    }

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
