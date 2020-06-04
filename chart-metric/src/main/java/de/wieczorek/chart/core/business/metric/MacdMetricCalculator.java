package de.wieczorek.chart.core.business.metric;

import de.wieczorek.chart.core.business.MetricCalculator;
import de.wieczorek.chart.core.persistence.ChartMetricId;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.ta4j.core.BarSeries;
import org.ta4j.core.aggregator.BaseBarSeriesAggregator;
import org.ta4j.core.aggregator.DurationBarAggregator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Map;
import java.util.function.BiConsumer;

@ApplicationScoped
public class MacdMetricCalculator implements MetricCalculator {

    @Override
    public ChartMetricRecord calculate(BarSeries timeSeries, Map<Integer, BiConsumer<ChartMetricRecord, Double>> config) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        int lastIndex = timeSeries.getEndIndex();

        ChartMetricRecord result = new ChartMetricRecord();
        ChartMetricId id = new ChartMetricId();
        id.setDate(timeSeries.getBar(lastIndex).getEndTime().toLocalDateTime());
        id.setIndicator("macd");
        result.setId(id);

        for (Map.Entry<Integer, BiConsumer<ChartMetricRecord, Double>> entry : config.entrySet()) {
            BaseBarSeriesAggregator aggregator = new BaseBarSeriesAggregator(new DurationBarAggregator(Duration.ofMinutes(entry.getKey()), false));
            BarSeries series = aggregator.aggregate(timeSeries);
            entry.getValue().accept(result, new MACDIndicator(closePrice, 12, 26).getValue(series.getEndIndex()).doubleValue());
        }

        return result;
    }

}
