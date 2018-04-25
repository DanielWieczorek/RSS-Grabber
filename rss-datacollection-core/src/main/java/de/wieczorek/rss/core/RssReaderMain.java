package de.wieczorek.rss.core;

import java.io.IOException;

import de.wieczorek.rss.core.config.RssConfig;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.ui.RssReaderServer;
import de.wieczorek.rss.core.weld.CdiContext;

public class RssReaderMain {

    public static void main(String[] args) throws IOException {

	CdiContext.INSTANCE.getBean(RssConfig.class);
	RssReaderServer server = CdiContext.INSTANCE.getBean(RssReaderServer.class);
	CdiContext.INSTANCE.getBean(RestInfoSender.class);
	server.start();
    }

}
