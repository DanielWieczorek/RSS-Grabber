package de.wieczorek.rss.trading.config;

import de.wieczorek.nn.NeuralNetworkName;
import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 22050;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;

    @Produces
    @ServiceName
    private String serviceName = "trader-config";

    @Produces
    @NeuralNetworkName
    private String neuralNetworkName = "rss-trader-policy";
}
