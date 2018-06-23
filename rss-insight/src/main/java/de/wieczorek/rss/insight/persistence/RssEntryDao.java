package de.wieczorek.rss.insight.persistence;
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
	return entityManager.createQuery("from RssEntry r where r.classification is not null", RssEntry.class) //
		.getResultList();
    }
}
