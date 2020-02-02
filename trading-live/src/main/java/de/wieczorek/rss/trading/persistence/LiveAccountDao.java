package de.wieczorek.rss.trading.persistence;

import de.wieczorek.core.persistence.EntityManagerProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class LiveAccountDao {

    public List<LiveAccount> find24h() {
        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0).minusHours(24);
        TypedQuery<LiveAccount> query = EntityManagerProvider.getEntityManager()
                .createQuery("SELECT s FROM LiveAccount s WHERE s.time >= :time", LiveAccount.class)
                .setParameter("time", time);
        return query.getResultList();
    }


    public void addAccountUpdate(LiveAccount trade) {
        EntityManagerProvider.getEntityManager().persist(trade);
    }
}
