package de.wieczorek.rss.core.recalculation;

import de.wieczorek.rss.core.persistence.EntityManagerHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

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

        return EntityManagerHelper.getSingleResultOrNull(allQuery);
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
