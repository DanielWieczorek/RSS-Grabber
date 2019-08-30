package de.wieczorek.rss.core.ui;

import de.wieczorek.rss.core.status.StatusUpdate;

import javax.enterprise.event.Observes;

public abstract class ControllerBase {

    protected void observe(@Observes StatusUpdate update) {
        switch (update.getStatus()) {
            case STARTED:
                start();
                break;
            case STOPPED:
                stop();
                break;
        }
    }

    protected void start() {
    }

    protected void stop() {
    }
}
