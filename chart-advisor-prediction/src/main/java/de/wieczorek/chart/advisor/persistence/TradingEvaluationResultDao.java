package de.wieczorek.chart.advisor.persistence;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.persistence.EntityManagerProvider;

@ApplicationScoped
public class TradingEvaluationResultDao {

    public TradingEvaluationResultDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
    }

    public void persist(TradingEvaluationResult sat) {
	EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
	transaction.begin();
		EntityManagerProvider.getEntityManager().persist(sat);
	transaction.commit();
    }

    public void update(TradingEvaluationResult sat) {
	EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
	transaction.begin();
		EntityManagerProvider.getEntityManager().merge(sat);
	transaction.commit();
    }

    public synchronized void upsert(TradingEvaluationResult sat) {
	TradingEvaluationResult found = findById(sat.getCurrentTime(), sat.getTargetTime());
	if (found == null) {
	    persist(sat);
	} else {
	    update(sat);
	}
    }

    public List<TradingEvaluationResult> findAll() {
	CriteriaBuilder cb = EntityManagerProvider.getEntityManager().getCriteriaBuilder();
	CriteriaQuery<TradingEvaluationResult> cq = cb.createQuery(TradingEvaluationResult.class);
	Root<TradingEvaluationResult> rootEntry = cq.from(TradingEvaluationResult.class);
	CriteriaQuery<TradingEvaluationResult> all = cq.select(rootEntry);
	TypedQuery<TradingEvaluationResult> allQuery = EntityManagerProvider.getEntityManager().createQuery(all);
	return allQuery.getResultList();
    }

    public TradingEvaluationResult findById(LocalDateTime currentTime, LocalDateTime targetTime) {
	TypedQuery<TradingEvaluationResult> query = EntityManagerProvider.getEntityManager().createQuery(
		"SELECT s FROM TradingEvaluationResult s WHERE s.currentTime = :current and s.targetTime = :target",
		TradingEvaluationResult.class).setParameter("current", currentTime).setParameter("target", targetTime);
	try {
	    return query.getSingleResult();
	} catch (NoResultException e) {
	    return null;
	}
    }

    public List<TradingEvaluationResult> findAfterDate(LocalDateTime currentTime) {
	TypedQuery<TradingEvaluationResult> query = EntityManagerProvider.getEntityManager().createQuery(
		"SELECT s FROM TradingEvaluationResult s WHERE s.currentTime > :current order by s.currentTime asc",
		TradingEvaluationResult.class).setParameter("current", currentTime);
	return query.getResultList();
    }

}
