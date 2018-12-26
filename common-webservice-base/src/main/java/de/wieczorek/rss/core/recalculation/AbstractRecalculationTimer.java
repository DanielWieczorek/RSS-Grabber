package de.wieczorek.rss.core.recalculation;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public abstract class AbstractRecalculationTimer implements Runnable {

    @Inject
    protected RecalculationStatusDao dao;

    @Override
    public void run() {
	try {
	    Recalculation recalculation = dao.find();
	    if (recalculation != null) {
		LocalDateTime nextStartDate = performRecalculation(recalculation.getLastDate());
		if (nextStartDate != null) {
		    System.out.println("starting recalculation at " + nextStartDate);
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
