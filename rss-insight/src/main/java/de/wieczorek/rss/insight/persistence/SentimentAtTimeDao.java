package de.wieczorek.rss.insight.persistence;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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

    public SentimentAtTime findById(LocalDateTime sentimentTime) {
	TypedQuery<SentimentAtTime> query = entityManager
		.createQuery("SELECT s FROM SentimentAtTime s WHERE s.sentimentTime = :time", SentimentAtTime.class)
		.setParameter("time", sentimentTime);
	try {
	    return query.getSingleResult();
	} catch (NoResultException e) {
	    return null;
	}
    }

    public List<SentimentAtTime> findAll() {
	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	CriteriaQuery<SentimentAtTime> cq = cb.createQuery(SentimentAtTime.class);
	Root<SentimentAtTime> rootEntry = cq.from(SentimentAtTime.class);
	CriteriaQuery<SentimentAtTime> all = cq.select(rootEntry);
	TypedQuery<SentimentAtTime> allQuery = entityManager.createQuery(all);
	return allQuery.getResultList();
    }

}
