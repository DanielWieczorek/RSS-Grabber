package de.wieczorek.chart.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class CredentialsDao {
    private EntityManager entityManager;

    public CredentialsDao() {
        final Map<String, String> props = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("authentication", props);
        entityManager = emf.createEntityManager();

    }

    public Credentials findByUsername(String username) {
        TypedQuery<Credentials> query = entityManager
                .createQuery("SELECT s FROM Credentials s WHERE s.username = :user", Credentials.class)
                .setParameter("user", username);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
