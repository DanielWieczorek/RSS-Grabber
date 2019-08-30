package de.wieczorek.chart.core.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SessionDao {
    private EntityManager entityManager;

    public SessionDao() {
        final Map<String, String> props = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("authentication", props);
        entityManager = emf.createEntityManager();

    }

    public void persist(Session session) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(session);
        transaction.commit();
    }

    public void update(Session session) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(session);
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
        TypedQuery<Session> query = entityManager
                .createQuery("SELECT s FROM Session s WHERE s.username = :user", Session.class)
                .setParameter("user", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Session> findInvalidSessions() {
        TypedQuery<Session> query = entityManager
                .createQuery("SELECT s FROM Session s WHERE s.expirationDate < :time", Session.class)
                .setParameter("time", LocalDateTime.now());
        return query.getResultList();
    }

    public void delete(Session session) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.remove(session);
        transaction.commit();
    }

}
