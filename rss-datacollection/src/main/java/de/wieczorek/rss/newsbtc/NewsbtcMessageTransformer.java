package de.wieczorek.rss.newsbtc;

import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.types.RssEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsbtcMessageTransformer implements MessageTransformer {
    private static final Logger logger = LoggerFactory.getLogger(NewsbtcMessageTransformer.class);

    @Override
    public RssEntry apply(RssEntry t) {
        String description = t.getDescription().replaceAll("\n", "").replace("... The post appeared first on NewsBTC.", "");
        try {

            t.setDescription(description);
            logger.info("extracted: " + description);
        } catch (Exception e) {
            logger.error("error extracting message from: " + description, e);
        }
        return t;
    }

}
