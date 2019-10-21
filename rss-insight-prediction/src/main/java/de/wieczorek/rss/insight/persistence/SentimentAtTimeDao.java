package de.wieczorek.rss.insight.persistence;

import de.wieczorek.rss.core.persistence.EntityManagerProvider;
import de.wieczorek.rss.insight.types.SentimentAtTime;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SentimentAtTimeDao {


    public void persist(SentimentAtTime sat) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(sat);
        transaction.commit();
    }

    public void update(SentimentAtTime sat) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(sat);
        transaction.commit();
    }

    public void upsert(SentimentAtTime sat) {
        SentimentAtTime found = findById(sat.getSentimentTime());
        if (found == null) {
            persist(sat);
        } else {
            update(sat);
        }
    }

    public SentimentAtTime findById(LocalDateTime sentimentTime) {
        TypedQuery<SentimentAtTime> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM SentimentAtTime s WHERE s.sentimentTime = :time", SentimentAtTime.class)
                .setParameter("time", sentimentTime);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<SentimentAtTime> findAll() {
        EntityManager em = EntityManagerProvider.getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SentimentAtTime> cq = cb.createQuery(SentimentAtTime.class);
        Root<SentimentAtTime> rootEntry = cq.from(SentimentAtTime.class);
        CriteriaQuery<SentimentAtTime> all = cq.select(rootEntry);
        TypedQuery<SentimentAtTime> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

}
