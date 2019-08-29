package de.wieczorek.rss.newsbtc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.types.RssEntry;

public class NewsbtcMessageTransformer implements MessageTransformer {
    private static final Logger logger = LoggerFactory.getLogger(NewsbtcMessageTransformer.class);

    @Override
    public RssEntry apply(RssEntry t) {
        String description = t.getDescription().replaceAll("\n", "");
        try {

            Pattern pattern = Pattern.compile(Pattern.quote("<p>") + "(.*?)" + Pattern.quote("<a"));

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
