package main.java.de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.common.DataGenerator;
import de.wieczorek.rss.trading.common.DataLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class DataGeneratorProducer {
    @Inject
    DataLoader dataLoader;


    @Produces
    public DataGenerator produceGenerator() {
        return new DataGenerator(
                dataLoader::loadAllSentiments,
                dataLoader::loadAllChartEntries,
                dataLoader::loadAllMetrics
        );
    }

}
