package de.wieczorek.rss.core.status;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class BootupListener {

    @Inject
    private Event<StatusUpdate> event;

    public void observeBootup(@Observes @Initialized(ApplicationScoped.class) Object init) {
        event.fire(StatusUpdate.started());
    }
}
