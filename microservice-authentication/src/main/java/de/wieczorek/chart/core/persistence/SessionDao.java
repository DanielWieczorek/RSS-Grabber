package de.wieczorek.chart.core.persistence;

import de.wieczorek.rss.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SessionDao {


    public void persist(Session session) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(session);
        transaction.commit();
    }

    public void update(Session session) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(session);
        transaction.commit();
    }

    public void upsert(Session session) {
        Session found = findByUsername(session.getUsername());
        if (found == null) {
            persist(session);
        } else {
            update(session);
        }
    }

    public Session findByUsername(String username) {
        TypedQuery<Session> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM Session s WHERE s.username = :user", Session.class)
                .setParameter("user", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Session> findInvalidSessions() {
        TypedQuery<Session> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM Session s WHERE s.expirationDate < :time", Session.class)
                .setParameter("time", LocalDateTime.now());
        return query.getResultList();
    }

    public void delete(Session session) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.remove(session);
        transaction.commit();
    }

}
