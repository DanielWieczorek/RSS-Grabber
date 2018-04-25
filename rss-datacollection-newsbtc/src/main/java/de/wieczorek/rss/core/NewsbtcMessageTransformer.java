package de.wieczorek.rss.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsbtcMessageTransformer implements MessageTransformer {

    @Override
    public RssEntry apply(RssEntry t) {
	String description = t.getDescription().replaceAll("\n", "");
	try {

	    Pattern pattern = Pattern.compile(Pattern.quote("<p>") + "(.*?)" + Pattern.quote("<a"));

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
