package de.wieczorek.rss.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.core.business.RssEntry;

public class BitcoinistMessageTransformer implements MessageTransformer {
    private static final Logger logger = LogManager.getLogger(BitcoinistMessageTransformer.class.getName());

    @Override
    public RssEntry apply(RssEntry t) {
	String description = t.getDescription().replaceAll("\n", "");
	try {

	    Pattern pattern = Pattern.compile("<p><img .*\\/>(.*)<\\/p>.*<p>.*<\\/p>");

	    Matcher matcher = pattern.matcher(description);
	    description = matcher.group(1);

	    t.setDescription(description);
	    logger.info("extracted: " + description);
	} catch (Exception e) {
	    logger.error("error extracting message from: " + description, e);
	}
	return t;
    }

}
