package de.wieczorek.rss.trading.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import de.wieczorek.nn.NeuralNetworkName;
import de.wieczorek.rss.core.config.ServiceName;
import de.wieczorek.rss.core.config.port.JGroupsPort;
import de.wieczorek.rss.core.config.port.RestPort;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 21020;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;

    @Produces
    @ServiceName
    private String serviceName = "rss-trader";

    @Produces
    @NeuralNetworkName
    private String neuralNetworkName = "rss-trader-policy";
}
