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
                itemVectors[j * 9 + 0][index] = Double.isNaN(record.get(j).getValue1min()) ? 0.0
                        : record.get(j).getValue1min();
                itemVectors[j * 9 + 1][index] = Double.isNaN(record.get(j).getValue5min()) ? 0.0
                        : record.get(j).getValue5min();
                itemVectors[j * 9 + 2][index] = Double.isNaN(record.get(j).getValue15min()) ? 0.0
                        : record.get(j).getValue15min();
                itemVectors[j * 9 + 3][index] = Double.isNaN(record.get(j).getValue30min()) ? 0.0
                        : record.get(j).getValue30min();
                itemVectors[j * 9 + 4][index] = Double.isNaN(record.get(j).getValue60min()) ? 0.0
                        : record.get(j).getValue60min();
                itemVectors[j * 9 + 5][index] = Double.isNaN(record.get(j).getValue2hour()) ? 0.0
                        : record.get(j).getValue2hour();
                itemVectors[j * 9 + 6][index] = Double.isNaN(record.get(j).getValue6hour()) ? 0.0
                        : record.get(j).getValue6hour();
                itemVectors[j * 9 + 7][index] = Double.isNaN(record.get(j).getValue12hour()) ? 0.0
                        : record.get(j).getValue12hour();
                itemVectors[j * 9 + 8][index] = Double.isNaN(record.get(j).getValue24hour()) ? 0.0
                        : record.get(j).getValue24hour();
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
        result.setPredictedDelta(output.getDouble(0));
        result.setAbsolutePrediction(input.getChartEntries().get(input.getDates().get(input.getEndIndex())).getClose()
                + result.getPredictedDelta());

        return result;
    }

}
