package de.wieczorek.rss.core.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.wieczorek.rss.core.business.RssReader;

@ApplicationScoped
public class Controller {
	// private boolean isStarted;

	@Inject
	private RssReader reader;

	public void start() {
		System.out.println("started");
		reader.start();
	}

	public void stop() {
		System.out.println("stopped");
		reader.stop();
	}

}
