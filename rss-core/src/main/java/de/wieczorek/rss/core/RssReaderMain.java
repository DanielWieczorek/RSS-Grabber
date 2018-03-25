package de.wieczorek.rss.core;

import java.io.IOException;

public class RssReaderMain {

    public static void main(String[] args) throws IOException {

	CdiContext.INSTANCE.getBean(RssConfig.class);
	RssReaderServer server = CdiContext.INSTANCE.getBean(RssReaderServer.class);
	server.start();
    }

}
