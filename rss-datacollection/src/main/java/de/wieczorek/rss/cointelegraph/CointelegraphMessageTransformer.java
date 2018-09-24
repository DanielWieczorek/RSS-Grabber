package de.wieczorek.rss.cointelegraph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.types.RssEntry;

public class CointelegraphMessageTransformer implements MessageTransformer {
    private static final Logger logger = LogManager.getLogger(CointelegraphMessageTransformer.class.getName());

    @Override
    public RssEntry apply(RssEntry t) {
	String description = t.getDescription().replaceAll("\n", "");
	try {

	    Pattern pattern = Pattern.compile(".*" + Pattern.quote("<p>") + "(.*)" + Pattern.quote("</p>"));

	    Matcher matcher = pattern.matcher(description);
	    matcher.find();
	    description = matcher.group(1);

	    t.setDescription(description);
	    logger.info("extracted: " + description);
	} catch (Exception e) {
	    logger.error("error extracting message from: " + description, e);
	}
	return t;
    }

}
