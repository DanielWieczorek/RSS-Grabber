package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.persistence.PerformedTrade;
import de.wieczorek.rss.trading.persistence.PerformedTradeDao;
import de.wieczorek.rss.trading.types.ActionVertexType;
import de.wieczorek.rss.trading.types.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class TradingContext implements Context {

    @Inject
    private PerformedTradeDao tradeDao;

    @Override
    public LocalDateTime getLastBuyTime() {
        PerformedTrade trade = tradeDao.findLastTrade();

        return trade.getType() == ActionVertexType.BUY ? trade.getTime() : null;
    }
}
