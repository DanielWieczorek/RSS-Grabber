package de.wieczorek.chart.core.persistence;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.importexport.db.ImportExportDao;

@ApplicationScoped
public class ChartMetricDao extends ImportExportDao<ChartMetricRecord> {

    public ChartMetricRecord findById(ChartMetricId id) {
	return EntityManagerProvider.getEntityManager().find(ChartMetricRecord.class, id);
    }

    public void mergeAll(Collection<ChartMetricRecord> entries) {
	EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
	transaction.begin();
	entries.forEach(EntityManagerProvider.getEntityManager()::merge);
	transaction.commit();
    }

    @Override
    public void persistAll(Collection<ChartMetricRecord> entries) {
	EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
	transaction.begin();
	entries.stream().forEach(EntityManagerProvider.getEntityManager()::persist);
	transaction.commit();
    }

    public List<ChartEntry> find24h() {
	LocalDateTime date = LocalDateTime.now().withSecond(0).withNano(0).minusHours(24);
	TypedQuery<ChartEntry> query = EntityManagerProvider.getEntityManager()
		.createQuery("SELECT s FROM ChartMetricRecord s WHERE s.id.date >= :time", ChartEntry.class)
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

    public void persist(ChartMetricRecord sat) {
	EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
	transaction.begin();
	EntityManagerProvider.getEntityManager().persist(sat);
	transaction.commit();
    }

    public void update(ChartMetricRecord sat) {
	EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
	transaction.begin();
	EntityManagerProvider.getEntityManager().merge(sat);
	transaction.commit();
    }

    public void upsert(ChartMetricRecord sat) {
	ChartMetricRecord found = findById(sat.getId());
	if (found == null) {
	    persist(sat);
	} else {
	    update(sat);
	}
    }

    @Override
    public Class<ChartMetricRecord> getEntityType() {
	return ChartMetricRecord.class;
    }

}
