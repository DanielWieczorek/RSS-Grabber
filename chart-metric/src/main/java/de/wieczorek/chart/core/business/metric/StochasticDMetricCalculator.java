package de.wieczorek.chart.core.business.metric;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.StochasticOscillatorDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.function.BiConsumer;

@ApplicationScoped
public class StochasticDMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(TimeSeries timeSeries, Map<Integer, BiConsumer<ChartMetricRecord, Double>> config) {
        ChartMetricRecord result = new ChartMetricRecord();
        int lastIndex = timeSeries.getEndIndex();

        ChartMetricId id = new ChartMetricId();
        id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
        id.setIndicator("stochasticD");
        result.setId(id);

        for (Map.Entry<Integer, BiConsumer<ChartMetricRecord, Double>> entry : config.entrySet()) {
            StochasticOscillatorKIndicator stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14 * entry.getKey());

            entry.getValue().accept(result, new StochasticOscillatorDIndicator(stochasticK).getValue(lastIndex).doubleValue());
        }
        return result;
    }

}
