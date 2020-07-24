package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.nn.AbstractNeuralNetworkPredictor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TradingNeuralNetworkPredictor
        extends AbstractNeuralNetworkPredictor<NetInputItem, TradingEvaluationResult> {

    @Override
    protected INDArray buildPredictionFeatures(NetInputItem item) {
        int maxLength = 4 * 60 + 1;
        int vectorSize = 4 * 9;

        double[][] itemVectors = new double[vectorSize][maxLength];
        int index = 0;
        INDArray features = Nd4j.create(new int[]{1, vectorSize, maxLength}, 'f');

        Map<LocalDateTime, List<ChartMetricRecord>> allRecords = item.getInputChartMetrics();
        List<LocalDateTime> ce = new ArrayList<>(allRecords.keySet());

        ce.sort(LocalDateTime::compareTo);
        for (int k = 0; k < ce.size(); k += 15) {

            List<ChartMetricRecord> record = allRecords.get(ce.get(k));
            if (record.size() != 4) {
                record = Arrays.asList(new ChartMetricRecord(), new ChartMetricRecord(), new ChartMetricRecord(),
                        new ChartMetricRecord());

            }

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
        final INDArray vectors = Nd4j.create(itemVectors);

        features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(),
                NDArrayIndex.interval(0, maxLength)}, vectors);
        return features;
    }

    @Override
    protected TradingEvaluationResult buildPredictionResult(NetInputItem input, INDArray output) {
        TradingEvaluationResult result = new TradingEvaluationResult();
        result.setPredictedDelta(Normalizer.denormalize(output.getDouble(0), Normalizer.getOutputBoundaries()));
        result.setAbsolutePrediction(input.getChartEntries().get(input.getDates().get(input.getEndIndex())).getClose()
                + result.getPredictedDelta());

        return result;
    }

}
