package de.wieczorek.rss.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import com.impetus.client.cassandra.common.CassandraConstants;

@ApplicationScoped
public class RssEntryDao {
    private EntityManager entityManager;

    public RssEntryDao() {
	final Map<String, String> props = new HashMap<>();
	props.put(CassandraConstants.CQL_VERSION, CassandraConstants.CQL_VERSION_3_0);
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu", props);
	entityManager = emf.createEntityManager();
	entityManager.setFlushMode(FlushModeType.COMMIT);
    }

    public void persist(List<RssEntry> entries) {
	EntityTransaction transaction = entityManager.getTransaction();
	transaction.begin();
	entries.stream().forEach(entityManager::persist);
	transaction.commit();
    }
}
