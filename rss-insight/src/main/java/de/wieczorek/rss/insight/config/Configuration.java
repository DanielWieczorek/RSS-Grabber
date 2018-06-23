package de.wieczorek.rss.insight.config;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class Configuration {

    @Produces
    @RestPort
    private int restPort = 10020;

}
