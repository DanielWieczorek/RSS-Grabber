package de.wieczorek.rss.trading.persistence;

import de.wieczorek.core.persistence.EntityManagerHelper;
import de.wieczorek.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PerformedTradeDao {


    public void addTrade(PerformedTrade trade) {
        EntityManagerProvider.getEntityManager().persist(trade);
    }

    public PerformedTrade find(String id) {
        return EntityManagerHelper.getSingleResultOrNull(
                EntityManagerProvider.getEntityManager()
                        .createQuery("select trade from PerformedTrade trade where trade.id = :id", PerformedTrade.class)
                        .setParameter("id", id));

    }

    public List<PerformedTrade> findAfter(LocalDateTime startTime) {
        TypedQuery<PerformedTrade> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM PerformedTrade s WHERE s.time >= :time", PerformedTrade.class)
                .setParameter("time", startTime);
        return query.getResultList();
    }

    public PerformedTrade findLastTrade() {
        TypedQuery<PerformedTrade> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM PerformedTrade s WHERE order by s.time", PerformedTrade.class)
                .setMaxResults(1);
        return EntityManagerHelper.getSingleResultOrNull(query);
    }

    public void update(PerformedTrade trade) {
        EntityManagerProvider.getEntityManager().merge(trade);
    }
}
