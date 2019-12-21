package de.wieczorek.rss.core.persistence;

import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.rss.classification.types.RssEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class RssEntryDao extends ImportExportDao<RssEntry> {

    public void persist(RssEntry entry) {
        EntityManagerProvider.getEntityManager().merge(entry);
    }

    public RssEntry find(RssEntry entry) {
        return EntityManagerProvider.getEntityManager()
                .createQuery("from RssEntry r where r.URI = :key", RssEntry.class) //
                .setParameter("key", entry.getURI()) //
                .getSingleResult();
    }

    public Date findNewestEntry() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select max(r.createdAt) from RssEntry r ", Date.class) //
                .getSingleResult();
    }

    public List<RssEntry> findAll(List<String> collect) {

        return EntityManagerProvider.getEntityManager()
                .createQuery("from RssEntry r where r.URI in :keys", RssEntry.class)//
                .setParameter("keys", collect).getResultList();
    }

    public List<RssEntry> findAllUnclassified(int maxResult) {
        return EntityManagerProvider.getEntityManager()
                .createQuery("from RssEntry r where r.classification is null", RssEntry.class) //
                .setMaxResults(maxResult) //
                .getResultList();
    }

    public List<RssEntry> findAllClassified() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select r from RssEntry r where r.classification is not null", RssEntry.class) //
                .getResultList();
    }

    @Override
    public void persistAll(Collection<RssEntry> entries) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        entries.forEach(em::persist);
    }

    @Override
    public Class<RssEntry> getEntityType() {
        return RssEntry.class;
    }

    @Override
    public List<RssEntry> findAll() {
        EntityManager em = EntityManagerProvider.getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RssEntry> cq = cb.createQuery(RssEntry.class);
        Root<RssEntry> rootEntry = cq.from(RssEntry.class);
        CriteriaQuery<RssEntry> all = cq.select(rootEntry);
        TypedQuery<RssEntry> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    public long countClassifiedEntries() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select count(r) from RssEntry r where r.classification is not null", Long.class) //
                .getSingleResult();
    }

    public long countUnclassifiedEntries() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select count(r) from RssEntry r where r.classification is null", Long.class) //
                .getSingleResult();
    }

}
