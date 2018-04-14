package de.wieczorek.rss.core;

import java.io.IOException;

import de.wieczorek.rss.core.jgroups.RestInfoSender;

public class RssReaderMain {

    public static void main(String[] args) throws IOException {

	CdiContext.INSTANCE.getBean(RssConfig.class);
	RssReaderServer server = CdiContext.INSTANCE.getBean(RssReaderServer.class);
	CdiContext.INSTANCE.getBean(RestInfoSender.class);
	server.start();
    }

}
