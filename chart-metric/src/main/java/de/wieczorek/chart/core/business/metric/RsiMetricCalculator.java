package de.wieczorek.chart.core.business.metric;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RsiMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(TimeSeries timeSeries) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        int lastIndex = timeSeries.getEndIndex();
        ChartMetricRecord result = new ChartMetricRecord();
        ChartMetricId id = new ChartMetricId();
        id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
        id.setIndicator("rsi");
        result.setId(id);

        result.setValue1min(new RSIIndicator(closePrice, 14).getValue(lastIndex).doubleValue());
        result.setValue5min(new RSIIndicator(closePrice, 14 * 5).getValue(lastIndex).doubleValue());
        result.setValue15min(new RSIIndicator(closePrice, 14 * 15).getValue(lastIndex).doubleValue());
        result.setValue30min(new RSIIndicator(closePrice, 14 * 30).getValue(lastIndex).doubleValue());
        result.setValue60min(new RSIIndicator(closePrice, 14 * 60).getValue(lastIndex).doubleValue());

        return result;
    }

}
