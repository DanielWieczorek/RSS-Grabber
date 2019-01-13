package de.wieczorek.chart.core.business;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ResponseProcessingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.persistence.Session;
import de.wieczorek.chart.core.persistence.SessionDao;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class SessionInvalidationTimer implements Runnable {
    private static final Logger logger = LogManager.getLogger(SessionInvalidationTimer.class.getName());

    @Inject
    private SessionDao dao;

    public SessionInvalidationTimer() {

    }

    @Override
    public void run() {
	try {
	    List<Session> invalidSessions = dao.findInvalidSessions();
	    invalidSessions.forEach(dao::delete);
	} catch (ResponseProcessingException e) {
	    logger.error("error while retrieving chart data: ", e.getResponse().readEntity(String.class));
	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	}
    }
}
