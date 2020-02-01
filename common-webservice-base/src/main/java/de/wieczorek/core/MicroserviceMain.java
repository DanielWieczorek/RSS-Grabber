package de.wieczorek.core;

import de.wieczorek.core.db.migration.DatabaseMigrator;
import de.wieczorek.core.jgroups.RestInfoSender;
import de.wieczorek.core.ui.MicroserviceServer;
import de.wieczorek.core.weld.CdiContext;

public class MicroserviceMain {

    public static void main(String[] args) throws Exception {

        MicroserviceServer server = CdiContext.INSTANCE.getBean(MicroserviceServer.class);

        CdiContext.INSTANCE.getBean(DatabaseMigrator.class).migrate();
        CdiContext.INSTANCE.getBean(RestInfoSender.class).init();
        server.start();
    }

}
