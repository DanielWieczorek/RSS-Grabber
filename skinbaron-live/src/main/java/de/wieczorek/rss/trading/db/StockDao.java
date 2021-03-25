package de.wieczorek.rss.trading.db;

import de.wieczorek.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class StockDao {

    public synchronized void persist(Stock entry) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        em.persist(entry);
    }

    public List<Stock> findAllCurrent() {
        TypedQuery<Stock> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM Stock s WHERE s.time = (select max(x.time) from Stock x)", Stock.class);
        return query.getResultList();
    }

}
