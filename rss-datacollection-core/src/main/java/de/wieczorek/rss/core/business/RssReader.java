package de.wieczorek.rss.core.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;

import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.persistence.RssEntryDao;
import de.wieczorek.rss.types.RssEntry;

public abstract class RssReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RssReader.class);

    @Inject
    private RssEntryDao dao;

    protected abstract RssConfig getRssConfig();

    @Override
    public void run() {
	RssConfig config = getRssConfig();
	logger.info("reading feed for " + config.getServiceName());

	try {
	    SyndFeed feed = buildFeed();
	    List<RssEntry> newEntries = feed.getEntries().stream().map(this::buildBo).filter(config.getFilter())
		    .map(config.getTransformer()).collect(Collectors.toList());
	    logger.info("new entries from " + config.getServiceName() + ": "
		    + newEntries.stream().map(RssEntry::getHeading).collect(Collectors.toList()));

	    if (!newEntries.isEmpty()) {
		List<String> existingEntryKeys = dao
			.findAll(newEntries.stream().map(RssEntry::getURI).collect(Collectors.toList())).stream()
			.map(RssEntry::getURI).collect(Collectors.toList());
		newEntries.removeAll(newEntries.stream().filter(entry -> existingEntryKeys.contains(entry.getURI()))
			.collect(Collectors.toList()));
		logger.info("persisting " + newEntries.size() + " entries");
		dao.persist(newEntries);
	    }

	} catch (Exception e) {
	    logger.error("error while retrieving rss feed entries: ", e);
	}
    }

    private SyndFeed buildFeed() throws IOException {
	InputStream is = null;
	SyndFeed feed = null;
	RssConfig config = getRssConfig();
	try {
	    System.setProperty("http.agent", "");
	    HttpURLConnection openConnection = (HttpURLConnection) new URL(config.getFeedUrl()).openConnection();
	    openConnection.setRequestProperty("User-Agent",
		    "Mozilla/5.0 (X11; U; CrOS i686 0.13.507) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.94 Safari/535.1");
	    is = openConnection.getInputStream();
	    if ("gzip".equals(openConnection.getContentEncoding())) {
		is = new GZIPInputStream(is);
	    }
	    InputSource source = new InputSource(is);
	    SyndFeedInput input = new SyndFeedInput();
	    feed = input.build(source);

	} catch (Exception e) {
	    logger.error("error while opening reading from feed: ", e);
	} finally {
	    if (is != null)
		is.close();
	}
	return feed;
    }

    private RssEntry buildBo(SyndEntry entry) {
	RssConfig config = getRssConfig();
	RssEntry e = new RssEntry();
	e.setDescription(entry.getDescription().getValue());
	e.setHeading(entry.getTitle());
	e.setURI(entry.getUri());
	e.setFeedUrl(config.getFeedUrl());
	e.setPublicationDate(entry.getPublishedDate());
	e.setCreatedAt(Date.from(Instant.now()));
	return e;
    }

}
