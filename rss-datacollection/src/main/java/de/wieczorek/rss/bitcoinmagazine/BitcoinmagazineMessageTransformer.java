package de.wieczorek.rss.bitcoinmagazine;

import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.types.RssEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitcoinmagazineMessageTransformer implements MessageTransformer {
    private static final Logger logger = LoggerFactory.getLogger(BitcoinmagazineMessageTransformer.class);

    @Override
    public RssEntry apply(RssEntry t) {
        String description = t.getDescription().replaceAll("\n", "");
        try {

            Pattern pattern = Pattern.compile("(.*?)");

            Matcher matcher = pattern.matcher(description);
            matcher.find();
            description = matcher.group(1);
            description = description.replaceAll("<.*?>", "");

            t.setDescription(description);
            logger.info("extracted: " + description);
        } catch (Exception e) {
            logger.error("error extracting message from: " + description, e);
        }
        return t;
    }

}
