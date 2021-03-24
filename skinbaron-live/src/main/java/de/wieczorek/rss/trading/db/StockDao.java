package de.wieczorek.rss.trading.db;

import de.wieczorek.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped
public class StockDao {

    public synchronized void persist(Stock entry) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        em.persist(entry);
    }

    public List<Stock> getAll() {
        EntityManager em = EntityManagerProvider.getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Stock> cq = cb.createQuery(Stock.class);
        Root<Stock> rootEntry = cq.from(Stock.class);
        CriteriaQuery<Stock> all = cq.select(rootEntry);
        TypedQuery<Stock> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

}
