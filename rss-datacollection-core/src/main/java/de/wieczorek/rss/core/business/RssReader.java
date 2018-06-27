package de.wieczorek.rss.core.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;

import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.persistence.RssEntryDao;

public class RssReader {
    private static final Logger logger = LogManager.getLogger(RssReader.class.getName());

    private RssEntryDao dao;

    private RssConfig config;

    private ScheduledExecutorService executor;

    public RssReader() {

    }

    public RssReader(RssConfig config, RssEntryDao dao) {
	this.config = config;
	this.dao = dao;
    }

    private void readRssFeed() {
	logger.info("reading feed for " + config.getServiceName());
	SyndFeed feed = null;
	try {
	    feed = buildFeed(feed);

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
		dao.persist(newEntries);
	    }

	} catch (Exception e) {
	    logger.error("error while retrieving rss feed entries: ", e);
	} finally {
	    executor.schedule(() -> readRssFeed(), 10, TimeUnit.MINUTES);
	}
    }

    private SyndFeed buildFeed(SyndFeed feed) throws IOException {
	InputStream is = null;
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
	RssEntry e = new RssEntry();
	e.setDescription(entry.getDescription().getValue());
	e.setHeading(entry.getTitle());
	e.setURI(entry.getUri());
	e.setFeedUrl(config.getFeedUrl());
	e.setPublicationDate(entry.getPublishedDate());
	return e;
    }

    public void start() {
	if (executor == null || executor.isShutdown()) {
	    executor = Executors.newScheduledThreadPool(1);
	}
	executor.execute(() -> readRssFeed());

    }

    public void stop() {
	executor.shutdownNow();
	try {
	    executor.awaitTermination(30, TimeUnit.SECONDS);
	} catch (InterruptedException e) {
	    logger.error("error stopping rss reader: ", e);
	}
    }
}
