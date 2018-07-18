package de.wieczorek.rss.insight.persistence;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import de.wieczorek.rss.insight.business.RssEntry;

@ApplicationScoped
public class RssEntryDao {
    private EntityManager entityManager;

    public RssEntryDao() {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
	entityManager = emf.createEntityManager();
	entityManager.setFlushMode(FlushModeType.COMMIT);

    }

    public List<RssEntry> findAllClassified() {
	return entityManager.createQuery("select r from RssEntry r where r.classification is not null", RssEntry.class) //
		.getResultList();
    }

    public List<RssEntry> findAll24h() {
	return entityManager
		.createQuery("select r from RssEntry r where r.publicationDate > :startDate", RssEntry.class) //
		.setParameter("startDate", Date.from(LocalDateTime.now().minusHours(24).toInstant(ZoneOffset.UTC)))
		.getResultList();

    }
}
