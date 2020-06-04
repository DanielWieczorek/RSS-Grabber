package de.wieczorek.chart.core.business.metric;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.BarSeries;
import org.ta4j.core.aggregator.BaseBarSeriesAggregator;
import org.ta4j.core.aggregator.DurationBarAggregator;
import org.ta4j.core.indicators.StochasticOscillatorDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Map;
import java.util.function.BiConsumer;

@ApplicationScoped
public class StochasticDMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(BarSeries timeSeries, Map<Integer, BiConsumer<ChartMetricRecord, Double>> config) {
        ChartMetricRecord result = new ChartMetricRecord();
        int lastIndex = timeSeries.getEndIndex();

        ChartMetricId id = new ChartMetricId();
        id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
        id.setIndicator("stochasticD");
        result.setId(id);

        for (Map.Entry<Integer, BiConsumer<ChartMetricRecord, Double>> entry : config.entrySet()) {
            BaseBarSeriesAggregator aggregator = new BaseBarSeriesAggregator(new DurationBarAggregator(Duration.ofMinutes(entry.getKey()), false));
            BarSeries series = aggregator.aggregate(timeSeries);

            StochasticOscillatorKIndicator stochasticK = new StochasticOscillatorKIndicator(timeSeries, 14);

            entry.getValue().accept(result, new StochasticOscillatorDIndicator(stochasticK).getValue(series.getEndIndex()).doubleValue());
        }
        return result;
    }

}
