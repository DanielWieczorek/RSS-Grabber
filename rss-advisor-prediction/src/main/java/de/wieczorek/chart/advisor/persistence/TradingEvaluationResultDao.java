package de.wieczorek.chart.advisor.persistence;

import de.wieczorek.chart.advisor.types.TradingEvaluationResult;
import de.wieczorek.rss.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TradingEvaluationResultDao {


    public void persist(TradingEvaluationResult sat) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(sat);
        transaction.commit();
    }

    public void update(TradingEvaluationResult sat) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(sat);
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
        EntityManager em = EntityManagerProvider.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TradingEvaluationResult> cq = cb.createQuery(TradingEvaluationResult.class);
        Root<TradingEvaluationResult> rootEntry = cq.from(TradingEvaluationResult.class);
        CriteriaQuery<TradingEvaluationResult> all = cq.select(rootEntry);
        TypedQuery<TradingEvaluationResult> allQuery = em.createQuery(all);
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
