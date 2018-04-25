package de.wieczorek.rss.core;

import java.io.IOException;

import de.wieczorek.rss.core.jgroups.RestInfoReceiver;

public class Main {

    public static void main(String[] args) throws IOException {

	CdiContext.INSTANCE.getBean(Configuration.class);
	Server server = CdiContext.INSTANCE.getBean(Server.class);
	CdiContext.INSTANCE.getBean(RestInfoReceiver.class);
	server.start();
    }

}
