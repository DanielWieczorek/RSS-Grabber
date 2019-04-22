package de.wieczorek.chart.core.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.wieczorek.importexport.db.ImportExportDao;

@ApplicationScoped
public class ChartMetricDao extends ImportExportDao<ChartMetricRecord> {
    private EntityManager entityManager;

    public ChartMetricDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
	entityManager = emf.createEntityManager();

    }

    public ChartMetricRecord findById(ChartMetricId id) {
	return entityManager.find(ChartMetricRecord.class, id);
    }

    @Override
    public void persistAll(Collection<ChartMetricRecord> entries) {
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entries.forEach(entityManager::persist);
	transaction.commit();
    }

    @Override
    public List<ChartMetricRecord> findAll() {
	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	CriteriaQuery<ChartMetricRecord> cq = cb.createQuery(ChartMetricRecord.class);
	Root<ChartMetricRecord> rootEntry = cq.from(ChartMetricRecord.class);
	CriteriaQuery<ChartMetricRecord> all = cq.select(rootEntry);
	TypedQuery<ChartMetricRecord> allQuery = entityManager.createQuery(all);
	return allQuery.getResultList();
    }

    @Override
    public Class<ChartMetricRecord> getEntityType() {
	return ChartMetricRecord.class;
    }

}
