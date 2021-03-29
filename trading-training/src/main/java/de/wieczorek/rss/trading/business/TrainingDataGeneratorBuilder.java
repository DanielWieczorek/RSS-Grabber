package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.io.DataLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class TrainingDataGeneratorBuilder implements DataGeneratorBuilder {
    @Inject
    private DataLoader dataLoader;

    private LocalDateTime startDate = LocalDateTime.now().minusDays(30);

    public DataGenerator produceGenerator() {
        return new DataGenerator(
                () -> dataLoader.loadSentiments("30d"),
                () -> dataLoader.loadChartEntries("30d"),
                () -> dataLoader.loadMetricSentiments("30d"),
                () -> dataLoader.loadMetrics("30d")
        );
    }
}
