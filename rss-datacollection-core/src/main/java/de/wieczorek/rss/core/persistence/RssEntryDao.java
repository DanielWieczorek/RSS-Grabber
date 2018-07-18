package de.wieczorek.rss.core.persistence;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import de.wieczorek.rss.core.business.RssEntry;

@ApplicationScoped
public class RssEntryDao {
    private EntityManager entityManager;

    public RssEntryDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
	entityManager = emf.createEntityManager();

    }

    public synchronized void persist(List<RssEntry> entries) {
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entries.stream().forEach(entityManager::persist);
	transaction.commit();
    }

    public List<RssEntry> findAll(List<String> collect) {

	return entityManager.createQuery("from RssEntry r where r.URI in :keys", RssEntry.class)//
		.setParameter("keys", collect).getResultList();
    }

    public List<RssEntry> findAllAfter(Date timestamp) {
	return entityManager.createQuery("from RssEntry r where r.createdAt > :after", RssEntry.class) //
		.setParameter("after", timestamp) //
		.getResultList();
    }

    public List<RssEntry> findAll24h() {
	return entityManager
		.createQuery("select r from RssEntry r where r.publicationDate > :startDate", RssEntry.class) //
		.setParameter("startDate", Date.from(LocalDateTime.now().minusHours(24).toInstant(ZoneOffset.UTC)))
		.getResultList();

    }

}
