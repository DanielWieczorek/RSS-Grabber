package de.wieczorek.rss.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.wieczorek.rss.core.business.MessageTransformer;
import de.wieczorek.rss.core.business.RssEntry;

public class BitcoincomMessageTransformer implements MessageTransformer {

    @Override
    public RssEntry apply(RssEntry t) {
	String description = t.getDescription().replaceAll("\n", "");
	try {

	    Pattern pattern = Pattern.compile(Pattern.quote("/>") + "(.*?)" + Pattern.quote("[&#8230;]"));

	    Matcher matcher = pattern.matcher(description);
	    matcher.find();
	    description = matcher.group(1);

	    t.setDescription(description);
	    System.out.println(description);
	} catch (Exception e) {
	    System.out.println(description);
	    e.printStackTrace();
	}
	return t;
    }

}
