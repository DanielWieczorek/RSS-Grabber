package de.wieczorek.chart.core.ui;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.persistence.ChartMetricDao;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.core.jgroups.RestInfoSender;
import de.wieczorek.rss.core.recalculation.Recalculation;
import de.wieczorek.rss.core.recalculation.RecalculationStatusDao;
import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(RestInfoSender.class);

    @Inject
    private RecurrentTaskManager timer;

    @Inject
    private ChartMetricDao dao;

    @Inject
    private RecalculationStatusDao recalculationDao;

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

    public List<ChartMetricRecord> getAll() {
        return dao.findAll();
    }

    public List<ChartMetricRecord> get24h() {
        return dao.find24h();
    }

    public List<ChartMetricRecord> getNow() {
        return dao.findNow();
    }

    public void recompute() {
        Recalculation recalculation = new Recalculation();
        recalculation.setLastDate(LocalDateTime.of(1900, 1, 1, 1, 1));
        recalculationDao.deleteAll();
        recalculationDao.create(recalculation);
    }

}
