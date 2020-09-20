package de.wieczorek.recalculation.type;

import de.wieczorek.core.persistence.EntityManagerHelper;
import de.wieczorek.core.persistence.EntityManagerProvider;
import de.wieczorek.recalculation.db.Recalculation;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@ApplicationScoped
public class RecalculationStatusDao {


    public Recalculation find() {
        CriteriaBuilder cb = EntityManagerProvider.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Recalculation> cq = cb.createQuery(Recalculation.class);
        Root<Recalculation> rootEntry = cq.from(Recalculation.class);
        CriteriaQuery<Recalculation> all = cq.select(rootEntry);
        TypedQuery<Recalculation> allQuery = EntityManagerProvider.getEntityManager().createQuery(all);

        return EntityManagerHelper.getSingleResultOrNull(allQuery);
    }

    public void update(Recalculation recalculation) {
        EntityManagerProvider.getEntityManager().merge(recalculation);
    }

    public void deleteAll() {
        Query query = EntityManagerProvider.getEntityManager().createQuery("DELETE FROM Recalculation");
        query.executeUpdate();
    }

    public void create(Recalculation recalculation) {
        EntityManagerProvider.getEntityManager().persist(recalculation);
    }

}
