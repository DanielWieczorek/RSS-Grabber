package de.wieczorek.rss.core;

import de.wieczorek.rss.core.jgroups.RestInfoReceiver;

public class Main {

    public static void main(String[] args) throws Exception {

	CdiContext.INSTANCE.getBean(Configuration.class);
	Server server = CdiContext.INSTANCE.getBean(Server.class);
	CdiContext.INSTANCE.getBean(RestInfoReceiver.class).init();
	server.start();
    }

}
