package de.wieczorek.rss.core.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.business.RssEntry;
import de.wieczorek.rss.core.persistence.RssEntryDao;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @Inject
    private RssEntryDao dao;

    public List<RssEntry> readUnclassifiedEntries() {
	logger.info("get all unclassified");
	return dao.findAllUnclassified(100);
    }

    public void updateClassification(RssEntry entry) {
	RssEntry found = dao.find(entry);
	found.setClassification(entry.getClassification());
	dao.persist(found);
    }
}
