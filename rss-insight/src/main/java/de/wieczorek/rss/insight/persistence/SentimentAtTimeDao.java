package de.wieczorek.rss.insight.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

@ApplicationScoped
public class SentimentAtTimeDao {
    private EntityManager entityManager;

    public SentimentAtTimeDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
	entityManager = emf.createEntityManager();
	entityManager.setFlushMode(FlushModeType.COMMIT);
    }

    public void persist(SentimentAtTime sat) {
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entityManager.persist(sat);
	transaction.commit();
    }

}
