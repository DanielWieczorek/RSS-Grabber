package de.wieczorek.rss.advisor.config;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.nn.NeuralNetworkName;
import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;
import de.wieczorek.rss.core.db.migration.MigrationConfiguration;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 12020;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;

    @Produces
    @ServiceName
    private String serviceName = "chart-advisor";

    @Produces
    @NeuralNetworkName
    private String neuralNetworkName = "chart-advisor-TradingNeuralNetwork";

    @Produces
    @MigrationConfiguration
    private Map<String, String> migrationConfig = Stream.of(new String[][] { //
	    { "flyway.url", "jdbc:postgresql://localhost/CHART_ADVISOR" }, //
	    { "flyway.user", "postgres" }, //
	    { "flyway.password", "admin" }, //
	    { "flyway.locations", "classpath:db/migration" } //
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
}
