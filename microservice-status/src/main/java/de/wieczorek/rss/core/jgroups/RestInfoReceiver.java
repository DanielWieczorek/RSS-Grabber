package de.wieczorek.rss.core.jgroups;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE3;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.MPING;
import org.jgroups.protocols.TCP;
import org.jgroups.protocols.UNICAST3;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.wieczorek.rss.core.config.port.JGroupsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RestInfoReceiver extends ReceiverAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RestInfoReceiver.class);

    private JChannel channel;
    private ObjectMapper objectMapper;

    private View oldView;

    @Inject
    private Event<StatusMessage> responseEvent;

    @Inject
    private Event<List<Address>> leftMembersEvent;

    @Inject
    @JGroupsPort
    private int port;

    @PostConstruct
    private void init() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            channel = new JChannel(new TCP() //
                    .setValue("bind_addr", Inet4Address.getLocalHost()) //
                    .setValue("bind_port", port) //
                    , new MPING() //
                    , new MERGE3() //
                    , new FD_SOCK() //
                    , new FD_ALL() //
                    .setValue("timeout", 12000) //
                    .setValue("interval", 3000) //
                    , new VERIFY_SUSPECT() //
                    , new BARRIER() //
                    , new NAKACK2() //
                    , new UNICAST3() //
                    , new STABLE() //
                    , new GMS() //
                    , new MFC() //
                    , new FRAG2());

            channel.setReceiver(this);
            channel.setDiscardOwnMessages(true);
            channel.connect("rss-collectors-rest");

            channel.send(new Message(null, objectMapper.writeValueAsBytes(new StatusRequest())));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //
    }

    @Override
    public void receive(Message msg) {

        try {
            StatusResponse response = objectMapper.readValue(msg.getBuffer(), StatusResponse.class);
            StatusMessage message = new StatusMessage();
            message.setAddress(message.getAddress());
            message.setResponse(response);

            responseEvent.fire(message);
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

        logger.debug(msg.getSrc() + ": " + msg.getObject());
    }

    @Override
    public void viewAccepted(View newView) {
        if (oldView != null) {
            leftMembersEvent.fire(View.leftMembers(oldView, newView));

            View.newMembers(oldView, newView).forEach(this::sendStatusRequest);
        } else {
            newView.getMembers().forEach(this::sendStatusRequest);
        }
        oldView = newView;
    }

    private void sendStatusRequest(Address address) {
        try {
            channel.send(new Message(address, objectMapper.writeValueAsBytes(new StatusRequest())));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}