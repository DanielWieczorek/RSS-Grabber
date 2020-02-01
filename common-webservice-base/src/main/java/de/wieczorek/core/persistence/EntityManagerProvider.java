package de.wieczorek.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;

public class EntityManagerProvider {
    private static EntityManagerFactory entityManagerFactoryHolder = Persistence.createEntityManagerFactory("rss", new HashMap<>());

    private static ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<>();

    public static void recreateEntityManager() {
        entityManagerHolder.set(entityManagerFactoryHolder.createEntityManager());
    }

    public static void destroyEntityManager() {
        entityManagerHolder.remove();
    }

    public static EntityManager getEntityManager() {
        return entityManagerHolder.get();

    }

}
