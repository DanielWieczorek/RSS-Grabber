package de.wieczorek.rss.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerProvider {
    private static ThreadLocal<EntityManagerFactory> entityManagerHolder = ThreadLocal.withInitial(() -> {
        final Map<String, String> props = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
        return emf;
    });

    public static EntityManager getEntityManager() {
        return entityManagerHolder.get().createEntityManager();

    }

}
