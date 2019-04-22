package de.wieczorek.chart.core.business.metric;

import javax.enterprise.context.ApplicationScoped;

import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.AroonOscillatorIndicator;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

@ApplicationScoped
public class AroonMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(TimeSeries timeSeries) {
	int lastIndex = timeSeries.getBarCount() - 1;
	ChartMetricRecord result = new ChartMetricRecord();

	ChartMetricId id = new ChartMetricId();
	id.setDate(timeSeries.getBar(lastIndex).getBeginTime().toLocalDateTime());
	id.setIndicator("aroon");
	result.setId(id);

	result.setValue1min(new AroonOscillatorIndicator(timeSeries, 25).getValue(lastIndex).doubleValue());
	result.setValue5min(new AroonOscillatorIndicator(timeSeries, 25 * 5).getValue(lastIndex).doubleValue());
	result.setValue15min(new AroonOscillatorIndicator(timeSeries, 25 * 15).getValue(lastIndex).doubleValue());
	result.setValue30min(new AroonOscillatorIndicator(timeSeries, 25 * 30).getValue(lastIndex).doubleValue());
	result.setValue60min(new AroonOscillatorIndicator(timeSeries, 25 * 60).getValue(lastIndex).doubleValue());

	return result;
    }

}
