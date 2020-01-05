package de.wieczorek.rss.core.ui;

import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

@ApplicationScoped
public class TargetProducer {


    @Produces
    private WebTarget produceTarget(InjectionPoint ip) {
        Target target = ip.getAnnotated().getAnnotation(Target.class);

        if (target != null) {
            Client client = ClientBuilder.newClient().register(new ObjectMapperContextResolver());
            switch (target.type()) {
                case LOCAL:
                    return client.target("http://localhost:" + target.port());
                case REMOTE:
                    return client.target("http://wieczorek.io:" + target.port());
            }
        }
        return null;
    }
}
