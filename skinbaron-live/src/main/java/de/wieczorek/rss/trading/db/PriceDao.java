package de.wieczorek.rss.trading.db;

import de.wieczorek.core.persistence.EntityManagerHelper;
import de.wieczorek.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

@ApplicationScoped
public class PriceDao {

    public synchronized void persist(Price entry) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        em.persist(entry);
    }

    public Double findMinOfLastWeek(long metaOfferId) {
        TypedQuery<Double> query = EntityManagerProvider.getEntityManager()
                .createQuery("select min(r.minimum) from Price r where r.time > :startDate and r.metaofferid = :metaofferid", Double.class) //
                .setParameter("startDate", LocalDateTime.now().minusDays(7))
                .setParameter("metaofferid", metaOfferId);


        return EntityManagerHelper.getSingleResultOrNull(query);
    }

    public Double findAvgOfLastWeek(long metaOfferId) {
        TypedQuery<Double> query = EntityManagerProvider.getEntityManager()
                .createQuery("select avg(r.minimum) from Price r where r.time > :startDate and r.metaofferid = :metaofferid", Double.class) //
                .setParameter("startDate", LocalDateTime.now().minusDays(7))
                .setParameter("metaofferid", metaOfferId);


        return EntityManagerHelper.getSingleResultOrNull(query);
    }
}
