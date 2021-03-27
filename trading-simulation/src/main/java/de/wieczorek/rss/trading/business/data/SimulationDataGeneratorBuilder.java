package de.wieczorek.rss.trading.business.data;

import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.io.DataLoader;
import de.wieczorek.rss.trading.common.io.SimulationContextProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SimulationDataGeneratorBuilder implements DataGeneratorBuilder {
    @Inject
    private DataLoader dataLoader;
    @Inject
    private SimulationContextProvider contextProvider;


    public DataGenerator produceGenerator() {
        return new DataGenerator(
                dataLoader::loadSentiments24h,
                dataLoader::loadChartEntries24h,
                dataLoader::loadMetricSentiments24h,
                dataLoader::loadMetrics24h,
                contextProvider
        );
    }

    public DataGenerator produceGenerator(String offset) {
        return new DataGenerator(
                () -> dataLoader.loadSentiments(offset),
                () -> dataLoader.loadChartEntries(offset),
                () -> dataLoader.loadMetricSentiments(offset),
                () -> dataLoader.loadMetrics(offset),
                contextProvider
        );
    }
}
