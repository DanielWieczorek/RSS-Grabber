package de.wieczorek.rss.trading.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wieczorek.core.config.ServiceName;
import de.wieczorek.core.config.port.JGroupsPort;
import de.wieczorek.core.config.port.RestPort;
import de.wieczorek.core.db.migration.MigrationConfiguration;
import de.wieczorek.nn.NeuralNetworkName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Configuration {
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);


    @Produces
    @RestPort
    private int restPort = 22050;
    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;
    @Produces
    @ServiceName
    private String serviceName = "trader-live";
    @Produces
    @NeuralNetworkName
    private String neuralNetworkName = "rss-trader-policy";
    @Produces
    @MigrationConfiguration
    private Map<String, String> migrationConfig = Stream.of(new String[][]{ //
            {"flyway.url", "jdbc:postgresql://localhost/TRADING_LIVE"}, //
            {"flyway.user", "postgres"}, //
            {"flyway.password", "admin"}, //
            {"flyway.locations", "classpath:db/migration"} //
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    @Produces
    private ServiceConfiguration getServiceConfiguration() throws IOException {
        return new ObjectMapper().readValue(buildConfigFilePath(), ServiceConfiguration.class);
    }

    private File buildConfigFilePath() {
        return Paths.get(System.getProperty("user.home"), "neural-networks", serviceName, CONFIG_FILE_NAME).toFile();
    }
}
