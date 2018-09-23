package de.wieczorek.chart.core.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.RssEntryDao;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;

@ApplicationScoped
public class Controller {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());
    private boolean isStarted;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
	start();
    }

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private RssEntryDao dao;

    public void start() {
	logger.info("started");
	timer.start();
	isStarted = true;
    }

    public void stop() {
	logger.info("stopped");
	timer.stop();
	isStarted = false;
    }

    public boolean isStarted() {
	return isStarted;
    }

    public List<ChartEntry> getAll() {
	return dao.findAll();
    }

}
