package de.wieczorek.chart.core.business;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.num.DoubleNum;

import de.wieczorek.chart.core.persistence.ChartMetricDao;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.recalculation.AbstractRecalculationTimer;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 30, unit = TimeUnit.SECONDS)
@ApplicationScoped
public class MetricRecalculationTimer extends AbstractRecalculationTimer {
    private static final Logger logger = LoggerFactory.getLogger(MetricRecalculationTimer.class);

    @Inject
    private ChartMetricDao dao;

    @Inject
    private Instance<MetricCalculator> metricCalculators;

    public MetricRecalculationTimer() {

    }

    @Override
    protected LocalDateTime performRecalculation(LocalDateTime startDate) {
	BaseTimeSeries series = new BaseTimeSeries("foo", DoubleNum.valueOf(0).function());
	List<ChartMetricRecord> records = new ArrayList<>();

	List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
		.target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
		.get(new GenericType<List<ChartEntry>>() {
		});
	chartEntries.sort(Comparator.comparing(ChartEntry::getDate));

	int index = 0;
	for (int i = 0; i < chartEntries.size(); i++) {
	    index = i;
	    if (chartEntries.get(i).getDate().isAfter(startDate)) {
		break;
	    }
	}

	int lastIndex = 0;

	for (int i = index; i < (6000 + index) && i < chartEntries.size(); i++) {
	    ChartEntry entry = chartEntries.get(i);

	    Bar b = new BaseBar(ZonedDateTime.of(entry.getDate(), ZoneId.of("UTC")), //
		    entry.getOpen(), //
		    entry.getHigh(), //
		    entry.getLow(), //
		    entry.getClose(), //
		    entry.getVolume(), //
		    DoubleNum.valueOf(0).function());
	    series.addBar(b);

	    metricCalculators.forEach(calculator -> {
		ChartMetricRecord record = calculator.calculate(series);

		if (dao.findById(record.getId()) == null) {
		    records.add(record);
		}
	    });
	    lastIndex = i;
	}

	dao.persistAll(records);

	if (lastIndex < chartEntries.size() - 1) {
	    return chartEntries.get(lastIndex).getDate();

	} else {
	    return null;

	}

    }
}
