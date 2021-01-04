package de.wieczorek.rss.trading.common.io;

import de.wieczorek.rss.trading.types.ContextProvider;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulationContextProvider implements ContextProvider {

    private ThreadLocal<SimulationContext> contextHolder = new ThreadLocal<>();

    public void createContext() {
        contextHolder.set(new SimulationContext());
    }

    @Override
    public SimulationContext getContext() {
        return contextHolder.get();
    }

    public void destroyContext() {
        contextHolder.remove();
    }
}
