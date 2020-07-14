package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.business.ui.ChartDataCollectionRemoteRestCaller;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.chart.core.persistence.ui.ChartMetricRemoteRestCaller;
import de.wieczorek.nn.IDataGenerator;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
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


            List<NetInputItem> data = new DataPreparator().withChartData(chartEntries).withMetrics(metrics).getData();
            List<NetInputItem> positive = data.stream().filter(item -> item.getOutputDelta() > 0).collect(Collectors.toList());
            List<NetInputItem> negative = data.stream().filter(item -> item.getOutputDelta() < 0).collect(Collectors.toList());

            int sizePerList = Math.min(positive.size(), negative.size());
            //sizePerList = Math.min(sizePerList, 100000);

            Collections.shuffle(positive);
            Collections.shuffle(negative);
            List<NetInputItem> trainingData = new ArrayList<>(positive.stream().limit(sizePerList).collect(Collectors.toList()));
            trainingData.addAll(negative.stream().limit(sizePerList).collect(Collectors.toList()));
            Collections.shuffle(trainingData);

            List<TrainingNetInputItem> netInput = new ArrayList<>();

            double stdDev = calculateStandardDeviation(trainingData.stream().map(item -> item.getOutputDelta()).collect(Collectors.toList()));
            int sizeBefore = trainingData.size();
            trainingData = trainingData.stream().filter(item -> Math.abs(item.getOutputDelta()) < 2.0 * stdDev).collect(Collectors.toList());

            logger.debug("removed " + (sizeBefore - trainingData.size()) + " outliers");

            for (int i = 0; i < trainingData.size(); i++) {
                NetInputItem item = trainingData.get(i);

                int maxLength = 24 * 4 + 1;
                int vectorSize = 4 * 9;
                double[][] itemVectors = new double[vectorSize][maxLength];
                int index = 0;

                List<LocalDateTime> dates = item.getDates().subList(item.getStartIndex(), item.getEndIndex());
                for (int k = 0; k < dates.size(); k += 15) {

                    List<ChartMetricRecord> record = item.getInputChartMetrics().get(dates.get(k));


                    if (record == null) {
                        itemVectors = null;
                        break;
                    }
                    if (record.size() != 4) {
                        record = Arrays.asList(new ChartMetricRecord(), new ChartMetricRecord(), new ChartMetricRecord(),
                                new ChartMetricRecord());
                    }

                    record.sort(Comparator.comparing(x -> x.getId().getIndicator()));
                    for (int j = 0; j < record.size(); j++) {
                        Normalizer.Boundaries b = new Normalizer.Boundaries(-1, 1);
                        if (record.get(j).getId() != null) {
                            b = Normalizer.getInputBoundaries(record.get(j).getId().getIndicator());
                        }

                        itemVectors[j * 9 + 0][index] = Normalizer.normalize(record.get(j).getValue1min(), b);
                        itemVectors[j * 9 + 1][index] = Normalizer.normalize(record.get(j).getValue5min(), b);
                        itemVectors[j * 9 + 2][index] = Normalizer.normalize(record.get(j).getValue15min(), b);
                        itemVectors[j * 9 + 3][index] = Normalizer.normalize(record.get(j).getValue30min(), b);
                        itemVectors[j * 9 + 4][index] = Normalizer.normalize(record.get(j).getValue60min(), b);
                        itemVectors[j * 9 + 5][index] = Normalizer.normalize(record.get(j).getValue2hour(), b);
                        itemVectors[j * 9 + 6][index] = Normalizer.normalize(record.get(j).getValue6hour(), b);
                        itemVectors[j * 9 + 7][index] = Normalizer.normalize(record.get(j).getValue12hour(), b);
                        itemVectors[j * 9 + 8][index] = Normalizer.normalize(record.get(j).getValue24hour(), b);
                    }
                    index++;

                }


                if (itemVectors != null) {
                    netInput.add(new TrainingNetInputItem(Nd4j.create(itemVectors), Normalizer.normalize(item.getOutputDelta(), Normalizer.getOutputBoundaries())));
                }
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
