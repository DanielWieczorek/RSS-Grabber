package de.wieczorek.chart.core.persistence;

import de.wieczorek.core.persistence.EntityManagerHelper;
import de.wieczorek.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class CredentialsDao {

    public Credentials findByUsername(String username) {
        TypedQuery<Credentials> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM Credentials s WHERE s.username = :user", Credentials.class)
                .setParameter("user", username);
        return EntityManagerHelper.getSingleResultOrNull(query);
    }

}
