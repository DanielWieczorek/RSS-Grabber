package de.wieczorek.rss.core;

import de.wieczorek.rss.core.db.migration.DatabaseMigrator;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.ui.MicroserviceServer;
import de.wieczorek.rss.core.weld.CdiContext;
import org.jboss.logging.MDC;

public class MicroserviceMain {

    public static void main(String[] args) throws Exception {

        MicroserviceServer server = CdiContext.INSTANCE.getBean(MicroserviceServer.class);

        CdiContext.INSTANCE.getBean(DatabaseMigrator.class).migrate();
        CdiContext.INSTANCE.getBean(RestInfoSender.class).init();
        server.start();
    }

}
