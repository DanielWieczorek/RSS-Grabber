package de.wieczorek.chart.core.ui;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartEntryDao;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private ChartEntryDao dao;

    @Override
    public void start() {
	logger.info("started");
	timer.start();
    }

    @Override
    public void stop() {
	logger.info("stopped");
	timer.stop();
    }

    public List<ChartEntry> getAll() {
	return dao.findAll();
    }

    public List<ChartEntry> get24h() {

	return dao.find24h();
    }

}
