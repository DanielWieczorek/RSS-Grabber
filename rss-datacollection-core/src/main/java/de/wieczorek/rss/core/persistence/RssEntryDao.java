package de.wieczorek.rss.core.persistence;

import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.rss.types.RssEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ApplicationScoped
public class RssEntryDao extends ImportExportDao<RssEntry> {


    public synchronized void persist(List<RssEntry> entries) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        entries.forEach(em::persist);
        transaction.commit();
    }

    public List<RssEntry> findAll(List<String> collect) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        return em.createQuery("from RssEntry r where r.URI in :keys", RssEntry.class)//
                .setParameter("keys", collect).getResultList();
    }

    public List<RssEntry> findAllAfter(Date timestamp) {
        return EntityManagerProvider.getEntityManager()
                .createQuery("from RssEntry r where r.createdAt > :after", RssEntry.class) //
                .setParameter("after", timestamp) //
                .getResultList();
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

    public List<RssEntry> findAll24h() {
        return EntityManagerProvider.getEntityManager()
                .createQuery("select r from RssEntry r where r.publicationDate > :startDate", RssEntry.class) //
                .setParameter("startDate", Date.from(LocalDateTime.now().minusHours(24).toInstant(ZoneOffset.UTC)))
                .getResultList();

    }

    @Override
    public Class<RssEntry> getEntityType() {
        return RssEntry.class;
    }

    @Override
    public void persistAll(Collection<RssEntry> entries) {
        EntityManager em = EntityManagerProvider.getEntityManager();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        entries.forEach(em::persist);
        transaction.commit();
    }

}
