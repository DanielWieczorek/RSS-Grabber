package de.wieczorek.rss.core.jgroups;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.RestPort;
import org.jgroups.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class RestInfoSender extends ReceiverAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RestInfoSender.class);

    @Inject
    private JChannel channel;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private Event<StatusMessage> responseEvent;

    @Inject
    @RestPort
    private int httpBindPort;

    @Inject
    @ServiceName
    private String collectorName;

    private View oldView;

    @Inject
    private Event<List<Address>> leftMembersEvent;

    public void init() throws Exception {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect("rss-collectors-rest");

    }

    @Override
    public void receive(Message msg) {
        TypeFactory tf = objectMapper.getTypeFactory();

        try {

            JGroupsMessage<?> message = objectMapper.readValue(msg.getBuffer(), JGroupsMessage.class);
            logger.info("Received message from " + msg.getSrc() + " of type " + message.type.getSimpleName());

            if (message.type == StatusRequest.class) {
                sendStatusResponse(msg);
            } else if (message.type == StatusResponse.class) {
                sendStatusResponseEvent(msg);
            }
        } catch (Exception e) {
            logger.info("error parsing message from " + msg.getSrc(), e);
        }
    }

    private void sendStatusResponseEvent(Message msg) throws IOException, JsonParseException, JsonMappingException {
        JGroupsMessage<?> message;
        message = objectMapper.readValue(msg.getBuffer(),
                objectMapper.getTypeFactory().constructParametricType(JGroupsMessage.class, StatusResponse.class));

        StatusResponse response = ((JGroupsMessage<StatusResponse>) message).payload;
        StatusMessage smg = new StatusMessage();
        smg.setAddress(msg.src());
        smg.setResponse(response);

        responseEvent.fire(smg);
    }

    private void sendStatusResponse(Message msg) throws Exception {
        JGroupsMessage<?> message = objectMapper.readValue(msg.getBuffer(),
                objectMapper.getTypeFactory().constructParametricType(JGroupsMessage.class, StatusRequest.class));
        StatusResponse response = new StatusResponse();
        response.setCollectorName(collectorName);
        response.setBindHostname("localhost");
        response.setBindPort(httpBindPort);

        JGroupsMessage<StatusResponse> outgoingMessage = new JGroupsMessage<>();
        outgoingMessage.type = StatusResponse.class;
        outgoingMessage.payload = response;

        channel.send(new Message(msg.getSrc(), objectMapper.writeValueAsBytes(outgoingMessage)));

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
            JGroupsMessage<StatusRequest> outgoingMessage = new JGroupsMessage<>();
            outgoingMessage.type = StatusRequest.class;
            outgoingMessage.payload = new StatusRequest();

            channel.send(new Message(address, objectMapper.writeValueAsBytes(outgoingMessage)));
            logger.debug("Sending status request to" + address.toString());

        } catch (JsonProcessingException e) {
            logger.error("failed to parse JSON", e);
        } catch (Exception e) {
            logger.error("Error while sending status request", e);
        }

    }
}