package de.wieczorek.chart.core.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import de.wieczorek.chart.core.business.ChartEntry;

@ApplicationScoped
public class RssEntryDao {
    private EntityManager entityManager;

    public RssEntryDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("chart", props);
	entityManager = emf.createEntityManager();

    }

    public ChartEntry findById(Date date) {
	return entityManager.find(ChartEntry.class, date);
    }

    public void persistAll(Collection<ChartEntry> entries) {
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entries.forEach(entityManager::persist);
	transaction.commit();
    }

}
