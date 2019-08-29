package de.wieczorek.rss.core.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.rss.classification.types.RssEntry;

@ApplicationScoped
public class RssEntryDao extends ImportExportDao<RssEntry> {
    private EntityManager entityManager;

    public RssEntryDao() {
        final Map<String, String> props = new HashMap<>();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rss", props);
        entityManager = emf.createEntityManager();
        entityManager.setFlushMode(FlushModeType.COMMIT);

    }

    public void persist(RssEntry entry) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(entry);
        transaction.commit();
    }

    public RssEntry find(RssEntry entry) {
        return entityManager.createQuery("from RssEntry r where r.URI = :key", RssEntry.class) //
                .setParameter("key", entry.getURI()) //
                .getSingleResult();
    }

    public Date findNewestEntry() {
        return entityManager.createQuery("select max(r.createdAt) from RssEntry r ", Date.class) //
                .getSingleResult();
    }

    public List<RssEntry> findAll(List<String> collect) {

        return entityManager.createQuery("from RssEntry r where r.URI in :keys", RssEntry.class)//
                .setParameter("keys", collect).getResultList();
    }

    public List<RssEntry> findAllUnclassified(int maxResult) {
        return entityManager.createQuery("from RssEntry r where r.classification is null", RssEntry.class) //
                .setMaxResults(maxResult) //
                .getResultList();
    }

    public synchronized void persist(List<RssEntry> entries) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entries.stream().forEach(entityManager::persist);
        transaction.commit();
    }

    public List<RssEntry> findAllClassified() {
        return entityManager.createQuery("select r from RssEntry r where r.classification is not null", RssEntry.class) //
                .getResultList();
    }

    @Override
    public void persistAll(Collection<RssEntry> entries) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entries.forEach(entityManager::persist);
        transaction.commit();
    }

    @Override
    public Class<RssEntry> getEntityType() {
        // TODO Auto-generated method stub
        return RssEntry.class;
    }

    @Override
    public List<RssEntry> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RssEntry> cq = cb.createQuery(RssEntry.class);
        Root<RssEntry> rootEntry = cq.from(RssEntry.class);
        CriteriaQuery<RssEntry> all = cq.select(rootEntry);
        TypedQuery<RssEntry> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }
}
