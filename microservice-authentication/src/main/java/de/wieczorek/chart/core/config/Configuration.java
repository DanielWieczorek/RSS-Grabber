package de.wieczorek.chart.core.config;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;
import de.wieczorek.rss.core.db.migration.MigrationConfiguration;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 32000;

    @Produces
    @JGroupsPort
    public int getJGroupsPort() {
        return restPort + 1;
    }

    @Produces
    @ServiceName
    private String serviceName = "microservice-authentication";

    @Produces
    @MigrationConfiguration
    private Map<String, String> migrationConfig = Stream.of(new String[][]{ //
            {"flyway.url", "jdbc:postgresql://localhost/MICROSERVICE_AUTHENTICATION"}, //
            {"flyway.user", "postgres"}, //
            {"flyway.password", "admin"}, //
            {"flyway.locations", "classpath:db/migration"} //
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
}
