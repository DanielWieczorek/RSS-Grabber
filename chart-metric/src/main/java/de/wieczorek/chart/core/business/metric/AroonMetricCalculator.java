package de.wieczorek.chart.core.business.metric;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.AroonOscillatorIndicator;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.function.BiConsumer;

@ApplicationScoped
public class AroonMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(TimeSeries timeSeries, Map<Integer, BiConsumer<ChartMetricRecord, Double>> config) {
        int lastIndex = timeSeries.getBarCount() - 1;
        ChartMetricRecord result = new ChartMetricRecord();

        ChartMetricId id = new ChartMetricId();
        id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
        id.setIndicator("aroon");
        result.setId(id);

        for (Map.Entry<Integer, BiConsumer<ChartMetricRecord, Double>> entry : config.entrySet()) {
            entry.getValue().accept(result, new AroonOscillatorIndicator(timeSeries, 25 * entry.getKey()).getValue(lastIndex).doubleValue());
        }

        return result;
    }

}
