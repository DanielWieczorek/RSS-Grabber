package de.wieczorek.chart.core.persistence;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.rss.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ChartEntryDao extends ImportExportDao<ChartEntry> {

    EntityManagerProvider.getEntityManager();

    public ChartEntryDao() {
        final Map<String, String> props = new HashMap<>();
    }

    public ChartEntry findById(LocalDateTime date) {
        TypedQuery<ChartEntry> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM ChartEntry s WHERE s.date = :time", ChartEntry.class)
                .setParameter("time", date);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void persistAll(Collection<ChartEntry> entries) {
        EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
        transaction.begin();
        entries.forEach(EntityManagerProvider.getEntityManager()::persist);
        transaction.commit();
    }

    @Override
    public List<ChartEntry> findAll() {
        CriteriaBuilder cb = EntityManagerProvider.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ChartEntry> cq = cb.createQuery(ChartEntry.class);
        Root<ChartEntry> rootEntry = cq.from(ChartEntry.class);
        CriteriaQuery<ChartEntry> all = cq.select(rootEntry);
        TypedQuery<ChartEntry> allQuery = EntityManagerProvider.getEntityManager().createQuery(all);
        return allQuery.getResultList();
    }

    public List<ChartEntry> find24h() {
        LocalDateTime date = LocalDateTime.now().withSecond(0).withNano(0).minusHours(24);
        TypedQuery<ChartEntry> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM ChartEntry s WHERE s.date >= :time", ChartEntry.class)
                .setParameter("time", date);
        return query.getResultList();
    }

    @Override
    public Class<ChartEntry> getEntityType() {
        return ChartEntry.class;
    }

}
