package de.wieczorek.rss.core.jgroups;

import org.jgroups.Address;

public class StatusMessage {

    private Address address;
    private StatusResponse response;

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    public StatusResponse getResponse() {
	return response;
    }

    public void setResponse(StatusResponse response) {
	this.response = response;
    }
}
