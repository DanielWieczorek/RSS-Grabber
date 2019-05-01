package de.wieczorek.chart.core.business.metric;

import javax.enterprise.context.ApplicationScoped;

import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;

@ApplicationScoped
public class MacdMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(TimeSeries timeSeries) {
	ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
	int lastIndex = timeSeries.getEndIndex();

	ChartMetricRecord result = new ChartMetricRecord();
	ChartMetricId id = new ChartMetricId();
	id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
	id.setIndicator("macd");
	result.setId(id);

	result.setValue1min(new MACDIndicator(closePrice, 12, 26).getValue(lastIndex).doubleValue());
	result.setValue5min(new MACDIndicator(closePrice, 12 * 5, 26 * 5).getValue(lastIndex).doubleValue());
	result.setValue15min(new MACDIndicator(closePrice, 12 * 15, 26 * 15).getValue(lastIndex).doubleValue());
	result.setValue30min(new MACDIndicator(closePrice, 12 * 30, 26 * 30).getValue(lastIndex).doubleValue());
	result.setValue60min(new MACDIndicator(closePrice, 12 * 60, 26 * 60).getValue(lastIndex).doubleValue());

	return result;
    }

}
