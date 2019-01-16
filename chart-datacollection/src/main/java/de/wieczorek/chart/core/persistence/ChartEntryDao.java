package de.wieczorek.chart.core.persistence;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.importexport.db.ExportDao;
import de.wieczorek.importexport.db.ImportDao;

@ApplicationScoped
public class ChartEntryDao implements ImportDao<ChartEntry>, ExportDao {
    private EntityManager entityManager;

    public ChartEntryDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("chart", props);
	entityManager = emf.createEntityManager();

    }

    public ChartEntry findById(LocalDateTime date) {
	TypedQuery<ChartEntry> query = entityManager
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
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entries.forEach(entityManager::persist);
	transaction.commit();
    }

    @Override
    public List<ChartEntry> findAll() {
	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	CriteriaQuery<ChartEntry> cq = cb.createQuery(ChartEntry.class);
	Root<ChartEntry> rootEntry = cq.from(ChartEntry.class);
	CriteriaQuery<ChartEntry> all = cq.select(rootEntry);
	TypedQuery<ChartEntry> allQuery = entityManager.createQuery(all);
	return allQuery.getResultList();
    }

    public List<ChartEntry> find24h() {
	LocalDateTime date = LocalDateTime.now().withSecond(0).withNano(0).minusHours(24);
	TypedQuery<ChartEntry> query = entityManager
		.createQuery("SELECT s FROM ChartEntry s WHERE s.date >= :time", ChartEntry.class)
		.setParameter("time", date);
	return query.getResultList();
    }

    @Override
    public Class<ChartEntry> getEntityType() {
	return ChartEntry.class;
    }

}
