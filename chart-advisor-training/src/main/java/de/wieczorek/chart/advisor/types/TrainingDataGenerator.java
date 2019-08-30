package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.nn.IDataGenerator;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class TrainingDataGenerator implements IDataGenerator<TrainingNetInputItem> {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);


    public List<TrainingNetInputItem> generate() {
        List<ChartMetricRecord> metrics = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:13000/metric/all").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartMetricRecord>>() {
                });

        List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
                .target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ChartEntry>>() {
                });

        if (metrics != null && chartEntries != null) {

            List<NetInputItem> data = new DataPreparator().withChartData(chartEntries).withMetrics(metrics).getData();
            List<NetInputItem> positive = data.stream().filter(item -> item.getOutputDelta() > 0).collect(Collectors.toList());
            List<NetInputItem> negative = data.stream().filter(item -> item.getOutputDelta() < 0).collect(Collectors.toList());

            int sizePerList = Math.min(positive.size(), negative.size());

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
                int vectorSize = 20;
                double[][] itemVectors = new double[vectorSize][maxLength];
                int index = 0;


                Map<ChartEntry, List<ChartMetricRecord>> allRecords = item.getInputChartMetrics();
                List<ChartEntry> ce = item.getChartEntries();
                for (int k = 0; k < ce.size(); k += 15) {

                    List<ChartMetricRecord> record = allRecords.get(ce.get(k));
                    if (record.size() != 4) {
                        record = Arrays.asList(new ChartMetricRecord(), new ChartMetricRecord(), new ChartMetricRecord(),
                                new ChartMetricRecord());

                    }
                    for (int j = 0; j < record.size(); j++) {
                        itemVectors[j * 5 + 0][index] = Double.isNaN(record.get(j).getValue1min()) ? 0.0
                                : record.get(j).getValue1min();
                        itemVectors[j * 5 + 1][index] = Double.isNaN(record.get(j).getValue5min()) ? 0.0
                                : record.get(j).getValue5min();
                        itemVectors[j * 5 + 2][index] = Double.isNaN(record.get(j).getValue15min()) ? 0.0
                                : record.get(j).getValue15min();
                        itemVectors[j * 5 + 3][index] = Double.isNaN(record.get(j).getValue30min()) ? 0.0
                                : record.get(j).getValue30min();
                        itemVectors[j * 5 + 4][index] = Double.isNaN(record.get(j).getValue60min()) ? 0.0
                                : record.get(j).getValue60min();
                    }
                    index++;

                }

                netInput.add(new TrainingNetInputItem(Nd4j.create(itemVectors), item.getOutputDelta()));
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
