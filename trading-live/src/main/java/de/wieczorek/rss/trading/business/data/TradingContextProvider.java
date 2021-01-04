package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.types.Context;
import de.wieczorek.rss.trading.types.ContextProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TradingContextProvider implements ContextProvider {

    @Inject
    private Context context;

    @Override
    public Context getContext() {
        return context;
    }
}
