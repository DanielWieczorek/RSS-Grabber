package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.common.DataGenerator;
import de.wieczorek.rss.trading.common.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.DataLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.xml.crypto.Data;

@ApplicationScoped
public class SimulationDataGeneratorBuilder implements DataGeneratorBuilder {
    @Inject
    private DataLoader dataLoader;


    public DataGenerator produceGenerator() {
        return new DataGenerator(
                dataLoader::loadSentiments24h,
                dataLoader::loadChartEntries24h,
                dataLoader::loadMetricSentiments24h
        );
    }

}
