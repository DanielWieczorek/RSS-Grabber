package de.wieczorek.rss.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitcoinistMessageTransformer implements MessageTransformer {

    @Override
    public RssEntry apply(RssEntry t) {
	String description = t.getDescription().replaceAll("\n", "");
	try {

	    Pattern pattern = Pattern.compile("<p><img .*\\/>(.*)<\\/p>.*<p>.*<\\/p>");

	    Matcher matcher = pattern.matcher(description);
	    System.out.println(matcher.find());
	    System.out.println(description);
	    System.out.println(matcher.groupCount());
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
