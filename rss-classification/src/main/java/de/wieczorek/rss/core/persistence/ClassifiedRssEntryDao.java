package de.wieczorek.rss.core.persistence;

import de.wieczorek.core.persistence.EntityManagerProvider;
import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;

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
public class ClassifiedRssEntryDao extends ImportExportDao<ClassifiedRssEntry> {

    public void persist(ClassifiedRssEntry entry) {
        EntityManagerProvider.getEntityManager().merge(entry);
    }

    public ClassifiedRssEntry find(ClassifiedRssEntry entry) {
        return EntityManagerProvider.getEntityManager()
                .createQuery("from ClassifiedRssEntry r where r.URI = :key", ClassifiedRssEntry.class) //
                .setParameter("key", entry.getURI()) //
                .getSingleResult();
    }

    public Date findNewestEntry() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select max(r.createdAt) from ClassifiedRssEntry r ", Date.class) //
                .getSingleResult();
    }

    public List<ClassifiedRssEntry> findAll(List<String> collect) {

        return EntityManagerProvider.getEntityManager()
                .createQuery("from ClassifiedRssEntry r where r.URI in :keys", ClassifiedRssEntry.class)//
                .setParameter("keys", collect).getResultList();
    }

    public List<ClassifiedRssEntry> findAllUnclassified(int maxResult) {
        return EntityManagerProvider.getEntityManager()
                .createQuery("from ClassifiedRssEntry r where r.classification is null", ClassifiedRssEntry.class) //
                .setMaxResults(maxResult) //
                .getResultList();
    }

    public List<ClassifiedRssEntry> findAllClassified() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select r from ClassifiedRssEntry r where r.classification is not null", ClassifiedRssEntry.class) //
                .getResultList();
    }

    @Override
    public void persistAll(Collection<ClassifiedRssEntry> entries) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        entries.forEach(em::persist);
    }

    @Override
    public Class<ClassifiedRssEntry> getEntityType() {
        return ClassifiedRssEntry.class;
    }

    @Override
    public List<ClassifiedRssEntry> findAll() {
        EntityManager em = EntityManagerProvider.getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ClassifiedRssEntry> cq = cb.createQuery(ClassifiedRssEntry.class);
        Root<ClassifiedRssEntry> rootEntry = cq.from(ClassifiedRssEntry.class);
        CriteriaQuery<ClassifiedRssEntry> all = cq.select(rootEntry);
        TypedQuery<ClassifiedRssEntry> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    public long countClassifiedEntries() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select count(r) from ClassifiedRssEntry r where r.classification is not null", Long.class) //
                .getSingleResult();
    }

    public long countUnclassifiedEntries() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select count(r) from ClassifiedRssEntry r where r.classification is null", Long.class) //
                .getSingleResult();
    }

}
