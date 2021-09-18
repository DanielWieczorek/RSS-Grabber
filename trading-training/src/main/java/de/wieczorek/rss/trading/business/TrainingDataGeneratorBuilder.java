package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.io.DataLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TrainingDataGeneratorBuilder implements DataGeneratorBuilder {
    @Inject
    private DataLoader dataLoader;

    public DataGenerator produceGenerator() {
        return new DataGenerator(
                () -> dataLoader.loadSentiments("300d"),
                () -> dataLoader.loadChartEntries("300d"),
                () -> dataLoader.loadMetricSentiments("300d"),
                () -> dataLoader.loadMetrics("300d")
        );
    }
}
