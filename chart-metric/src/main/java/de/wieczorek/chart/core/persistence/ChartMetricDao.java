package de.wieczorek.chart.core.persistence;

import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.rss.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class ChartMetricDao extends ImportExportDao<ChartMetricRecord> {

    public ChartMetricRecord findById(ChartMetricId id) {
        return EntityManagerProvider.getEntityManager().find(ChartMetricRecord.class, id);
    }

    @Override
    public void persistAll(Collection<ChartMetricRecord> entries) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        entries.forEach(em::persist);
        transaction.commit();
    }

    public List<ChartMetricRecord> find24h() {
        LocalDateTime date = LocalDateTime.now().withSecond(0).withNano(0).minusHours(24);
        TypedQuery<ChartMetricRecord> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM ChartMetricRecord s WHERE s.id.date >= :time", ChartMetricRecord.class)
                .setParameter("time", date);
        return query.getResultList();
    }

    public List<ChartMetricRecord> findNow() {
        LocalDateTime date = LocalDateTime.now().withSecond(0).withNano(0).minusMinutes(1);
        TypedQuery<ChartMetricRecord> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM ChartMetricRecord s WHERE s.id.date = :time", ChartMetricRecord.class)
                .setParameter("time", date);
        return query.getResultList();
    }

    @Override
    public List<ChartMetricRecord> findAll() {
        CriteriaBuilder cb = EntityManagerProvider.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ChartMetricRecord> cq = cb.createQuery(ChartMetricRecord.class);
        Root<ChartMetricRecord> rootEntry = cq.from(ChartMetricRecord.class);
        CriteriaQuery<ChartMetricRecord> all = cq.select(rootEntry);
        TypedQuery<ChartMetricRecord> allQuery = EntityManagerProvider.getEntityManager().createQuery(all);
        return allQuery.getResultList();
    }

    public void upsert(List<ChartMetricRecord> sat) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        sat.forEach(x -> {
            ChartMetricRecord found = findById(x.getId());
            if (found == null) {
                em.persist(x);
            } else {
                em.merge(x);
            }
        });
        transaction.commit();

    }

    @Override
    public Class<ChartMetricRecord> getEntityType() {
        return ChartMetricRecord.class;
    }

}
