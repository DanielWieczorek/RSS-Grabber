package de.wieczorek.rss.trading.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wieczorek.core.config.ServiceName;
import de.wieczorek.core.config.port.JGroupsPort;
import de.wieczorek.core.config.port.RestPort;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class Configuration {
    private static final String CONFIG_FILE_NAME = "config.json";

    @Produces
    @RestPort
    private int restPort = 32050;
    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;
    @Produces
    @ServiceName
    private String serviceName = "skinbaron-live";


    @Produces
    private ServiceConfiguration getServiceConfiguration() throws IOException {
        return new ObjectMapper().readValue(buildConfigFilePath(), ServiceConfiguration.class);
    }

    private File buildConfigFilePath() {
        return Paths.get(System.getProperty("user.home"), "neural-networks", serviceName, CONFIG_FILE_NAME).toFile();
    }
}
