package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.persistence.Session;
import de.wieczorek.chart.core.persistence.SessionDao;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ResponseProcessingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class SessionInvalidationTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SessionInvalidationTimer.class);

    @Inject
    private SessionDao dao;

    @Override
    public void run() {
        try {
            List<Session> invalidSessions = dao.findInvalidSessions();
            invalidSessions.forEach(dao::delete);
        } catch (ResponseProcessingException e) {
            logger.error("error while invalidating session ", e.getResponse().readEntity(String.class));
        } catch (Exception e) {
            logger.error("error while invalidating session: ", e);
        }
    }
}
