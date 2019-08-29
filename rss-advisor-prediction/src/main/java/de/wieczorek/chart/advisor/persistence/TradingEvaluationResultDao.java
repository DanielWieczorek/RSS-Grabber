package de.wieczorek.chart.advisor.persistence;

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

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;

@ApplicationScoped
public class TradingEvaluationResultDao {
    private EntityManager entityManager;

    public TradingEvaluationResultDao() {
        final Map<String, String> props = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
        entityManager = emf.createEntityManager();
        entityManager.setFlushMode(FlushModeType.COMMIT);
    }

    public void persist(TradingEvaluationResult sat) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(sat);
        transaction.commit();
    }

    public void update(TradingEvaluationResult sat) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(sat);
        transaction.commit();
    }

    public void upsert(TradingEvaluationResult sat) {
        TradingEvaluationResult found = findById(sat.getCurrentTime(), sat.getTargetTime());
        if (found == null) {
            persist(sat);
        } else {
            update(sat);
        }
    }

    public List<TradingEvaluationResult> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TradingEvaluationResult> cq = cb.createQuery(TradingEvaluationResult.class);
        Root<TradingEvaluationResult> rootEntry = cq.from(TradingEvaluationResult.class);
        CriteriaQuery<TradingEvaluationResult> all = cq.select(rootEntry);
        TypedQuery<TradingEvaluationResult> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }

    public TradingEvaluationResult findById(LocalDateTime currentTime, LocalDateTime targetTime) {
        TypedQuery<TradingEvaluationResult> query = entityManager.createQuery(
                "SELECT s FROM TradingEvaluationResult s WHERE s.currentTime = :current and s.targetTime = :target",
                TradingEvaluationResult.class).setParameter("current", currentTime).setParameter("target", targetTime);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<TradingEvaluationResult> findAfterDate(LocalDateTime currentTime) {
        TypedQuery<TradingEvaluationResult> query = entityManager.createQuery(
                "SELECT s FROM TradingEvaluationResult s WHERE s.currentTime > :current order by s.currentTime asc",
                TradingEvaluationResult.class).setParameter("current", currentTime);
        return query.getResultList();
    }

}
