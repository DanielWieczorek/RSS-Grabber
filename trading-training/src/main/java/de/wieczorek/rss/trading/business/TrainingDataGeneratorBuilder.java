package de.wieczorek.rss.trading.business;

import de.wieczorek.rss.trading.common.io.DataGenerator;
import de.wieczorek.rss.trading.common.io.DataGeneratorBuilder;
import de.wieczorek.rss.trading.common.io.DataLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@ApplicationScoped
public class TrainingDataGeneratorBuilder implements DataGeneratorBuilder {
    @Inject
    private DataLoader dataLoader;

    private LocalDateTime startDate = LocalDateTime.now().minusMonths(3);

    public DataGenerator produceGenerator() {
        return new DataGenerator(
                () -> Collections.emptyList(),//dataLoader.loadAllSentiments().stream().filter(sentiment -> sentiment.getCurrentTime().isAfter(startDate)).collect(Collectors.toList()),
                () -> dataLoader.loadAllChartEntries().stream().filter(sentiment -> sentiment.getDate().isAfter(startDate)).collect(Collectors.toList()),
                () -> dataLoader.loadAllMetricSentiments().stream().filter(sentiment -> sentiment.getCurrentTime().isAfter(startDate)).collect(Collectors.toList())
        );
    }
}
