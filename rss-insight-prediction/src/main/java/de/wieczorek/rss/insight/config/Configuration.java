package de.wieczorek.rss.insight.config;

import de.wieczorek.nn.NeuralNetworkName;
import de.wieczorek.core.config.ServiceName;
import de.wieczorek.core.config.port.JGroupsPort;
import de.wieczorek.core.config.port.RestPort;
import de.wieczorek.core.db.migration.MigrationConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 11020;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;

    @Produces
    @ServiceName
    private String serviceName = "rss-insight";

    @Produces
    @NeuralNetworkName
    private String neuralNetworkName = "rss-insight-RssSentimentNeuralNetwork";

    @Produces
    @MigrationConfiguration
    private Map<String, String> migrationConfig = Stream.of(new String[][]{ //
            {"flyway.url", "jdbc:postgresql://localhost/RSS_INSIGHT"}, //
            {"flyway.user", "postgres"}, //
            {"flyway.password", "admin"}, //
            {"flyway.locations", "classpath:db/migration"} //
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
}
