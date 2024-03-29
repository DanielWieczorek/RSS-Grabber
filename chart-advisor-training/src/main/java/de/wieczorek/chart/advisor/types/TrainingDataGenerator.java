package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionRemoteRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.ChartMetricRemoteRestCaller;
import de.wieczorek.nn.IDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TrainingDataGenerator implements IDataGenerator<TrainingNetInputItem> {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private ChartMetricRemoteRestCaller chartMetricCaller;

    @Inject
    private ChartDataCollectionRemoteRestCaller chartDataCollectionCaller;


    public List<TrainingNetInputItem> generate() {
        List<ChartMetricRecord> metrics = chartMetricCaller.metricAll();

        List<ChartEntry> chartEntries = chartDataCollectionCaller.ohlcv();

        if (metrics != null && chartEntries != null) {

            metrics = metrics.stream().filter(entry ->
                    !(entry.getValue24hour() == 0.0
                            && entry.getValue12hour() == 0.0
                            && entry.getValue6hour() == 0.0
                            && entry.getValue2hour() == 0.0)).collect(Collectors.toList());


            List<NetInputItem> data = new DataPreparator().withChartData(chartEntries).withMetrics(metrics).getData();
            data.forEach(item -> item.setOutputDelta(Math.round(item.getOutputDelta())));

            List<NetInputItem> positive = data.stream().filter(item -> item.getOutputDelta() > 0).collect(Collectors.toList());
            List<NetInputItem> negative = data.stream().filter(item -> item.getOutputDelta() < 0).collect(Collectors.toList());

            int sizePerList = Math.min(positive.size(), negative.size());

            Collections.shuffle(positive);
            Collections.shuffle(negative);
            List<NetInputItem> trainingData = positive.stream().limit(sizePerList).collect(Collectors.toList());
            trainingData.addAll(negative.stream().limit(sizePerList).collect(Collectors.toList()));
            Collections.shuffle(trainingData);

            List<TrainingNetInputItem> netInput = new ArrayList<>();

            double stdDev = calculateStandardDeviation(trainingData.stream().map(NetInputItem::getOutputDelta).collect(Collectors.toList()));
            int sizeBefore = trainingData.size();
            trainingData = trainingData.stream().filter(item -> Math.abs(item.getOutputDelta()) < 2.0 * stdDev).collect(Collectors.toList());

            logger.debug("removed " + (sizeBefore - trainingData.size()) + " outliers");

            for (int i = 0; i < trainingData.size(); i++) {
                NetInputItem item = trainingData.get(i);

                double[][] itemVectors = NetworkInputBuilder.getVectors(item);

                netInput.add(new TrainingNetInputItem(itemVectors, Normalizer.normalize(item.getOutputDelta(), Normalizer.getOutputBoundaries())));

                logger.debug("preparing data " + i);
            }
            return netInput;
        }
        return null;
    }


    private double calculateStandardDeviation(List<Double> sd) {

        double sum = 0;
        double newSum = 0;

        for (Double aDouble : sd) {
            sum = sum + aDouble;
        }
        double mean = (sum) / (sd.size());

        for (Double aDouble : sd) {
            // put the calculation right in there
            newSum = newSum + ((aDouble - mean) * (aDouble - mean));
        }
        double squaredDiffMean = (newSum) / (sd.size());

        return (Math.sqrt(squaredDiffMean));
    }


}
