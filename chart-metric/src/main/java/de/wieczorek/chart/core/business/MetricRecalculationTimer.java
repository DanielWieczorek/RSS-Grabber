package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.business.ui.ChartDataCollectionLocalRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricDao;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.recalculation.AbstractRecalculationTimer;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RecurrentTask(interval = 30, unit = TimeUnit.SECONDS)
@EntityManagerContext
@ApplicationScoped
public class MetricRecalculationTimer extends AbstractRecalculationTimer {
    private static final Logger logger = LoggerFactory.getLogger(MetricRecalculationTimer.class);

    @Inject
    private ChartMetricDao dao;

    @Inject
    private Instance<MetricCalculator> metricCalculators;

    @Inject
    private ChartDataCollectionLocalRestCaller caller;

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {

        List<ChartEntry> chartEntries = caller.ohlcv();
        chartEntries = chartEntries.stream().distinct().sorted(Comparator.comparing(ChartEntry::getDate))
                .collect(Collectors.toList());

        int seriesEndIndex = 0;
        for (int i = 0; i < chartEntries.size(); i++) {
            seriesEndIndex = i;
            if (chartEntries.get(i).getDate().isAfter(startDate)) {
                break;
            }
        }

        int seriesStartIndex = 0;
        for (int i = 0; i < chartEntries.size(); i++) {
            seriesStartIndex = i;
            if (chartEntries.get(i).getDate().isAfter(startDate.minusDays(24))) {
                break;
            }
        }


        int lastIndex = 0;
        List<ChartMetricRecord> records = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            BaseTimeSeries series = new BaseTimeSeries("foo", DoubleNum.valueOf(0).function());

            List<ChartEntry> entries = chartEntries.subList(seriesStartIndex + i, seriesEndIndex + i);
            entries.forEach(entry -> {
                Bar b = new BaseBar(ZonedDateTime.of(entry.getDate(), ZoneId.of("UTC")), //
                        entry.getOpen(), //
                        entry.getHigh(), //
                        entry.getLow(), //
                        entry.getClose(), //
                        entry.getVolume(), //
                        DoubleNum.valueOf(0).function());
                series.addBar(b);
            });
            metricCalculators.forEach(calculator -> records.add(calculator.calculate(series, DurationFieldMappingHolder.configs)));
            lastIndex = i;
        }

        dao.upsert(records);

        if (lastIndex < chartEntries.size() - 1) {
            return chartEntries.get(lastIndex).getDate();
        } else {
            return null;

        }

    }
}
