package de.wieczorek.rss.core.jgroups;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class RestInfoSender extends ReceiverAdapter {
    private static final Logger logger = LogManager.getLogger(RestInfoSender.class.getName());

    private JChannel channel;
    ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    @JGroupsPort
    private int bindPort;

    @Inject
    @RestPort
    private int httpBindPort;

    @Inject
    @ServiceName
    private String collectorName;

    public RestInfoSender() throws Exception {
	channel = new JChannel();
	channel.connect("rss-collectors-rest");
	channel.setReceiver(this);
	channel.setDiscardOwnMessages(true);
    }

    @Override
    public void receive(Message msg) {

	try {
	    objectMapper.readValue(msg.getBuffer(), StatusRequest.class);
	    StatusResponse response = new StatusResponse();
	    response.setCollectorName(collectorName);
	    response.setBindHostname("localhost");
	    response.setBindPort(httpBindPort);
	    channel.send(new Message(msg.getSrc(), objectMapper.writeValueAsBytes(response)));
	} catch (Exception e) {
	    logger.error("error parsing message from " + msg.getSrc(), e);
	}
	logger.info("Received message from " + msg.getSrc() + ": " + msg.getObject());
    }

}