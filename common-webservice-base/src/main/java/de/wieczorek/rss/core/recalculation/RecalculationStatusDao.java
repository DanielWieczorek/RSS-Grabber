package de.wieczorek.rss.core.recalculation;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@ApplicationScoped
public class RecalculationStatusDao {

    private EntityManager entityManager;

    public RecalculationStatusDao() {
        final Map<String, String> props = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
        entityManager = emf.createEntityManager();
        entityManager.setFlushMode(FlushModeType.COMMIT);
    }

    public Recalculation find() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recalculation> cq = cb.createQuery(Recalculation.class);
        Root<Recalculation> rootEntry = cq.from(Recalculation.class);
        CriteriaQuery<Recalculation> all = cq.select(rootEntry);
        TypedQuery<Recalculation> allQuery = entityManager.createQuery(all);
        try {
            return allQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void update(Recalculation recalculation) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(recalculation);
        transaction.commit();
    }

    public void deleteAll() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Query query = entityManager.createQuery("DELETE FROM Recalculation");
        query.executeUpdate();
        transaction.commit();

    }

    public void create(Recalculation recalculation) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(recalculation);
        transaction.commit();
    }

}
