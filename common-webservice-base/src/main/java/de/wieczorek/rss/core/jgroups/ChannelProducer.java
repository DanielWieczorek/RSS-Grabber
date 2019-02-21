package de.wieczorek.rss.core.jgroups;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jgroups.JChannel;
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

import de.wieczorek.rss.core.config.port.JGroupsPort;

@ApplicationScoped
public class ChannelProducer {

    @Inject
    @JGroupsPort
    private int bindPort;

    @Produces
    private JChannel buildInfoChannel() throws UnknownHostException, Exception {
	return new JChannel(new TCP() //
		.setValue("bind_addr", Inet4Address.getLoopbackAddress()) //
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
    }

}
