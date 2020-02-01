package de.wieczorek.core.recalculation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public abstract class AbstractRecalculationTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRecalculationTimer.class);

    @Inject
    protected RecalculationStatusDao dao;

    @Override
    public void run() {
        try {
            Recalculation recalculation = dao.find();
            if (recalculation != null) {
                LocalDateTime nextStartDate = performRecalculation(recalculation.getLastDate());
                if (nextStartDate != null) {
                    logger.debug("starting recalculation at " + nextStartDate);
                    recalculation.setLastDate(nextStartDate);
                    dao.update(recalculation);
                } else {
                    dao.deleteAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract LocalDateTime performRecalculation(LocalDateTime startDate);

}
