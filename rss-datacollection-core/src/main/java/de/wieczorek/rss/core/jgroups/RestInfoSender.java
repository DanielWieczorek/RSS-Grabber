package de.wieczorek.rss.core.jgroups;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
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

    public void init() throws Exception {

	channel = new JChannel(new TCP() //
		.setValue("bind_port", bindPort) //
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
		, new FRAG2()); //

	channel.setReceiver(this);
	channel.setDiscardOwnMessages(true);
	channel.connect("rss-collectors-rest");
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