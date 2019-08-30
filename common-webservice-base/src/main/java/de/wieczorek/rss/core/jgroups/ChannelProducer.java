package de.wieczorek.rss.core.jgroups;

import de.wieczorek.rss.core.config.port.JGroupsPort;
import org.jgroups.JChannel;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.Inet4Address;
import java.net.UnknownHostException;

@ApplicationScoped
public class ChannelProducer {

    @Inject
    @JGroupsPort
    private int bindPort;

    @Produces
    private JChannel buildInfoChannel() throws UnknownHostException, Exception {
        return new JChannel(new TCP() //
                .setValue("bind_addr", Inet4Address.getLocalHost()) //
                .setValue("bind_port", bindPort) //
                , new JDBC_PING().setValue("connection_driver", "org.postgresql.Driver")//
                .setValue("connection_password", "admin")//
                .setValue("connection_url", "jdbc:postgresql://localhost/MICROSERVICE_COMMON")//
                .setValue("connection_username", "postgres")//
                .setValue("initialize_sql",
                        "CREATE TABLE JGROUPSPING (" + "own_addr varchar(200) NOT NULL, "
                                + "cluster_name varchar(200) NOT NULL, " + "ping_data bytea DEFAULT NULL, "
                                + "PRIMARY KEY (own_addr, cluster_name) )"),
                new MERGE3() //
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
