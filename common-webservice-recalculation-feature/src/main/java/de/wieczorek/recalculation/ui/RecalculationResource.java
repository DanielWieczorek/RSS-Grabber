package de.wieczorek.recalculation.ui;

import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import de.wieczorek.recalculation.db.Recalculation;
import de.wieczorek.recalculation.type.RecalculationStatusDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;

@Resource
@EntityManagerContext
@Path("recalculation")
@ApplicationScoped
public class RecalculationResource {

    @Inject
    private RecalculationStatusDao recalculationDao;

    @GET
    @Path("start")
    public void start() {
        Recalculation recalculation = new Recalculation();
        recalculation.setLastDate(LocalDateTime.of(1900, 1, 1, 1, 1));
        recalculationDao.deleteAll();
        recalculationDao.create(recalculation);
    }

    @GET
    @Path("stop")
    public void stop() {
        recalculationDao.deleteAll();
    }

    @GET
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public Recalculation status() {
        return recalculationDao.find();
    }
}
