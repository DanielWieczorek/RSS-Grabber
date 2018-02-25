package de.wieczorek.rss.core;

public class RssReaderMain {

    public static void main(String[] args) {
	CdiContext.INSTANCE.getBean(RssReaderConfigurationProvider.class);
	RssReaderServer server = CdiContext.INSTANCE.getBean(RssReaderServer.class);
	server.start();
    }

}
