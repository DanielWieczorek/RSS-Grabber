package de.wieczorek.rss.insight;

import de.wieczorek.rss.insight.ui.ClassificationServer;
import de.wieczorek.rss.insight.weld.CdiContext;

public class Application {

    public static void main(String[] args) throws Exception {

	ClassificationServer server = CdiContext.INSTANCE.getBean(ClassificationServer.class);
	server.start();
    }

}
