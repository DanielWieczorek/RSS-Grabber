package de.wieczorek.chart.core.business.metric;

import javax.enterprise.context.ApplicationScoped;

import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.StochasticOscillatorDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

@ApplicationScoped
public class StochasticDMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(TimeSeries timeSeries) {
	ChartMetricRecord result = new ChartMetricRecord();
	int lastIndex = timeSeries.getEndIndex();

	ChartMetricId id = new ChartMetricId();
	id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
	id.setIndicator("stochasticD");
	result.setId(id);

	StochasticOscillatorKIndicator stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14);
	result.setValue1min(new StochasticOscillatorDIndicator(stochasticK).getValue(lastIndex).doubleValue());

	stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14 * 5);
	result.setValue5min(new StochasticOscillatorDIndicator(stochasticK).getValue(lastIndex).doubleValue());

	stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14 * 15);
	result.setValue15min(new StochasticOscillatorDIndicator(stochasticK).getValue(lastIndex).doubleValue());

	stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14 * 30);
	result.setValue30min(new StochasticOscillatorDIndicator(stochasticK).getValue(lastIndex).doubleValue());

	stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14 * 60);
	result.setValue60min(new StochasticOscillatorDIndicator(stochasticK).getValue(lastIndex).doubleValue());
	return result;
    }

}
