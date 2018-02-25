package de.wieczorek.rss.core;

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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xml.sax.InputSource;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;

@ApplicationScoped
public class RssReader {

    @Inject
    @FeedUrl
    private String feedUrl = "";
    @Inject
    private RssEntryDao dao;

    @Inject
    private MessageFilter filter;

    @Inject
    private MessageTransformer transformer;

    private ScheduledExecutorService executor;

    public RssReader() {

    }

    private void readRssFeed() {
	SyndFeed feed = null;
	try {
	    feed = buildFeed(feed);

	    List<RssEntry> entries = feed.getEntries().stream().map(this::buildBo).filter(filter).map(transformer)
		    .collect(Collectors.toList());
	    dao.persist(entries);

	    executor.schedule(() -> readRssFeed(), 10, TimeUnit.MINUTES);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private SyndFeed buildFeed(SyndFeed feed) throws IOException {
	InputStream is = null;
	try {
	    System.setProperty("http.agent", "");
	    HttpURLConnection openConnection = (HttpURLConnection) new URL(feedUrl).openConnection();
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
	    e.printStackTrace();
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
	e.setFeedUrl(feedUrl);
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
