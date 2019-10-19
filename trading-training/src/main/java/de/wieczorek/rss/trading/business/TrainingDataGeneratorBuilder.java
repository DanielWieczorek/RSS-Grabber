package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.common.DataGenerator;
import de.wieczorek.rss.trading.common.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.DataLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TrainingDataGeneratorBuilder implements DataGeneratorBuilder {
    @Inject
    private DataLoader dataLoader;


    public DataGenerator produceGenerator() {
        return new DataGenerator(
                dataLoader::loadAllSentiments,
                dataLoader::loadAllChartEntries,
                dataLoader::loadAllMetricSentiments
        );
    }

}
