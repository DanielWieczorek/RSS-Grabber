package de.wieczorek.chart.core.persistence;

import de.wieczorek.rss.core.persistence.EntityManagerHelper;
import de.wieczorek.rss.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SessionDao {


    public void persist(Session session) {
        EntityManagerProvider.getEntityManager().persist(session);
    }

    public void update(Session session) {
        EntityManagerProvider.getEntityManager().merge(session);
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
        return EntityManagerHelper.getSingleResultOrNull(query);
    }

    public List<Session> findInvalidSessions() {
        TypedQuery<Session> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM Session s WHERE s.expirationDate < :time", Session.class)
                .setParameter("time", LocalDateTime.now());
        return query.getResultList();
    }

    public void delete(Session session) {
        EntityManagerProvider.getEntityManager().remove(session);
    }

}
