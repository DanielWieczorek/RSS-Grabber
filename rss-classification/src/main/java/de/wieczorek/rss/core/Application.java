package de.wieczorek.rss.core;

import de.wieczorek.rss.core.ui.ClassificationServer;
import de.wieczorek.rss.core.weld.CdiContext;

public class Application {

    public static void main(String[] args) throws Exception {

	ClassificationServer server = CdiContext.INSTANCE.getBean(ClassificationServer.class);
	server.start();
    }

}
