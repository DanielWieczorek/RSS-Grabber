package de.wieczorek.rss.core.jgroups;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.JGroupsPort;
import de.wieczorek.rss.core.ServiceName;

@ApplicationScoped
public class RestInfoSender extends ReceiverAdapter {

    private JChannel channel;
    ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    @JGroupsPort
    private int bindPort;

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
	    StatusRequest command = objectMapper.readValue(msg.getBuffer(), StatusRequest.class);
	    StatusResponse response = new StatusResponse();
	    response.setCollectorName(collectorName);
	    response.setBindHostname("localhost");
	    response.setBindPort(bindPort);
	    channel.send(new Message(msg.getSrc(), objectMapper.writeValueAsBytes(response)));
	} catch (

	JsonParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

}