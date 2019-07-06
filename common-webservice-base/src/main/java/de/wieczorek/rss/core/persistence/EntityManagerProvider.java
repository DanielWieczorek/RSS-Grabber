package de.wieczorek.rss.core.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {
    private static ThreadLocal<EntityManager> entityManagerHolder = ThreadLocal.withInitial(() -> {
	final Map<String, String> props = new HashMap<>();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
	return emf.createEntityManager();
    });

    public static EntityManager getEntityManager() {
	return entityManagerHolder.get();

    }

}
