package de.wieczorek.rss.core.ui;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

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
	Date newest = dao.findNewestEntry();
	if (newest == null) {
	    newest = new Date(0);
	}

	List<RssEntry> newEntries = ClientBuilder.newClient()
		.target("http://localhost:8020/rss-entries/" + newest.toInstant().getEpochSecond())
		.request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
		});

	if (!newEntries.isEmpty()) {
	    List<String> existingEntryKeys = dao
		    .findAll(newEntries.stream().map(RssEntry::getURI).collect(Collectors.toList())).stream()
		    .map(RssEntry::getURI).collect(Collectors.toList());
	    newEntries.removeAll(newEntries.stream().filter(entry -> existingEntryKeys.contains(entry.getURI()))
		    .collect(Collectors.toList()));
	    logger.info("persisting " + newEntries.size() + " entries");
	    dao.persist(newEntries);
	}

	return dao.findAllUnclassified(100);
    }

    public void updateClassification(RssEntry entry) {
	RssEntry found = dao.find(entry);
	found.setClassification(entry.getClassification());
	dao.persist(found);
    }

    public List<RssEntry> readClassfiedEntries() {
	return dao.findAllClassified();
    }
}
