package de.wieczorek.core.status;

public class StatusUpdate {

    private ServiceState status;

    private StatusUpdate() {

    }

    public ServiceState getStatus() {
        return status;
    }

    static StatusUpdate started() {
        StatusUpdate result = new StatusUpdate();
        result.status = ServiceState.STARTED;
        return result;
    }

    static StatusUpdate stopped() {
        StatusUpdate result = new StatusUpdate();
        result.status = ServiceState.STOPPED;
        return result;
    }
}
