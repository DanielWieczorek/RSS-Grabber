package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricDao;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.num.DoubleNum;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RecurrentTask(interval = 30, unit = TimeUnit.SECONDS)
@EntityManagerContext
@ApplicationScoped
public class MetricTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MetricTimer.class);

    @Inject
    private ChartMetricDao dao;

    @Inject
    private Instance<MetricCalculator> metricCalculators;

    @Inject
    private ChartDataCollectionLocalRestCaller caller;

    @Override
    public void run() {
        List<ChartEntry> chartEntries = caller.ohlcv24d();
        chartEntries.sort(Comparator.comparing(ChartEntry::getDate));

        BaseTimeSeries series = new BaseTimeSeries("foo", DoubleNum.valueOf(0).function());
        chartEntries.forEach(entry -> {
            Bar b = new BaseBar(ZonedDateTime.of(entry.getDate(), ZoneId.of("UTC")), //
                    entry.getOpen(), //
                    entry.getHigh(), //
                    entry.getLow(), //
                    entry.getClose(), //
                    entry.getVolume(), //
                    DoubleNum.valueOf(0).function());
            series.addBar(b);
        });


        List<ChartMetricRecord> records = new ArrayList<>();

        metricCalculators.forEach(calculator ->
                records.add(calculator.calculate(series, DurationFieldMappingHolder.configs))
        );

        dao.upsert(records);

    }
}
