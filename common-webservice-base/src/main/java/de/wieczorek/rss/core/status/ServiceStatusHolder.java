package de.wieczorek.rss.core.status;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class ServiceStatusHolder {

    private ServiceState status;

    public ServiceState getStatus() {
        return status;
    }

    protected void observe(@Observes StatusUpdate update) {
        status = update.getStatus();
    }

}
