package de.wieczorek.rss.core;

import org.jboss.logging.MDC;

import de.wieczorek.rss.core.db.migration.DatabaseMigrator;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.ui.RssReaderServer;
import de.wieczorek.rss.core.weld.CdiContext;

public class RssReaderMain {

    public static void main(String[] args) throws Exception {

	RssReaderServer server = CdiContext.INSTANCE.getBean(RssReaderServer.class);

	MDC.put("service", "foo");
	CdiContext.INSTANCE.getBean(DatabaseMigrator.class).migrate();
	CdiContext.INSTANCE.getBean(RestInfoSender.class).init();
	server.start();
    }

}
