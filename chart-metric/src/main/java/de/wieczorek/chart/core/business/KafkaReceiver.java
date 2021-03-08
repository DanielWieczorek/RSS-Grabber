package de.wieczorek.chart.core.business;

import com.google.common.base.Stopwatch;
import de.wieczorek.chart.core.business.kafka.ChartEntryTopicConfiguration;
import de.wieczorek.chart.core.business.kafka.ChartMetricTopicConfiguration;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricDao;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.core.kafka.KafkaSender;
import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.DoubleNum;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@ApplicationScoped
@EntityManagerContext
@WithTopicConfiguration(configName = ChartEntryTopicConfiguration.class)
public class KafkaReceiver implements Consumer<ChartEntry> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @Inject
    private ChartMetricDao dao;

    @Inject
    private Instance<MetricCalculator> metricCalculators;

    @Inject
    private ChartDataCollectionLocalRestCaller caller;

    @Inject
    @WithTopicConfiguration(configName = ChartMetricTopicConfiguration.class)
    private KafkaSender<Object> sender;

    @Override
    public void accept(ChartEntry foo) {
        List<ChartEntry> chartEntries = caller.ohlcv24d();
        chartEntries.sort(Comparator.comparing(ChartEntry::getDate));

        Stopwatch sw1 = Stopwatch.createStarted();
        BarSeries series = new BaseBarSeries("foo", DoubleNum.valueOf(0).function());
        chartEntries.forEach(entry -> {
            Bar b = new BaseBar(Duration.ofMinutes(1), ZonedDateTime.of(entry.getDate(), ZoneId.of("UTC")), //
                    entry.getOpen(), //
                    entry.getHigh(), //
                    entry.getLow(), //
                    entry.getClose(), //
                    entry.getVolume());
            series.addBar(b);
        });


        logger.debug("creating the time series: " + sw1.elapsed().toSeconds());

        List<ChartMetricRecord> records = new ArrayList<>();

        sw1 = Stopwatch.createStarted();
        metricCalculators.forEach(calculator ->
                records.add(calculator.calculate(series, DurationFieldMappingHolder.configs))
        );
        logger.debug("calculating metrics took: " + sw1.elapsed().toSeconds());


        sw1 = Stopwatch.createStarted();
        dao.upsert(records);
        logger.debug("persisting took: " + sw1.elapsed().toSeconds());

        ChartMetricRecord record = records.get(records.size() - 1);
        sender.send(record.getId().toString(), record);
    }


}
