package de.wieczorek.rss.insight.config;

import de.wieczorek.nn.NeuralNetworkName;
import de.wieczorek.core.config.ServiceName;
import de.wieczorek.core.config.port.JGroupsPort;
import de.wieczorek.core.config.port.RestPort;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 11120;

    @Produces
    @JGroupsPort
    private int jgroupsPort = restPort + 1;

    @Produces
    @ServiceName
    private String serviceName = "rss-insight";

    @Produces
    @NeuralNetworkName
    private String neuralNetworkName = "rss-insight-RssSentimentNeuralNetwork";
}
