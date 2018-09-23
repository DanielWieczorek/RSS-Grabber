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
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.wieczorek.chart.core.business.ChartEntry;

@ApplicationScoped
public class RssEntryDao {
    private EntityManager entityManager;

    public RssEntryDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("chart", props);
	entityManager = emf.createEntityManager();

    }

    public ChartEntry findById(LocalDateTime date) {
	return entityManager.find(ChartEntry.class, date);
    }

    public void persistAll(Collection<ChartEntry> entries) {
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entries.forEach(entityManager::persist);
	transaction.commit();
    }

    public List<ChartEntry> findAll() {
	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	CriteriaQuery<ChartEntry> cq = cb.createQuery(ChartEntry.class);
	Root<ChartEntry> rootEntry = cq.from(ChartEntry.class);
	CriteriaQuery<ChartEntry> all = cq.select(rootEntry);
	TypedQuery<ChartEntry> allQuery = entityManager.createQuery(all);
	return allQuery.getResultList();
    }

}
