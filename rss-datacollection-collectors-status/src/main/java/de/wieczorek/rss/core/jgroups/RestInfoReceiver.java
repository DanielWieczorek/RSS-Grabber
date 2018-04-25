package de.wieczorek.rss.core.jgroups;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ApplicationScoped
public class RestInfoReceiver extends ReceiverAdapter {

    private JChannel channel;
    ObjectMapper objectMapper;

    @Inject
    private Event<StatusResponse> responseEvent;

    public RestInfoReceiver() throws Exception {
	objectMapper = new ObjectMapper();
	objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

	channel = new JChannel();
	channel.connect("rss-collectors-rest");
	channel.setReceiver(this);
	channel.setDiscardOwnMessages(true);

	channel.send(new Message(null, objectMapper.writeValueAsBytes(new StatusRequest())));
    }

    @Override
    public void receive(Message msg) {

	try {
	    StatusResponse command = objectMapper.readValue(msg.getBuffer(), StatusResponse.class);
	    responseEvent.fire(command);
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

    @Override
    public void viewAccepted(View newView) {
	System.out.println(newView);
	try {
	    channel.send(new Message(null, objectMapper.writeValueAsBytes(new StatusRequest())));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}