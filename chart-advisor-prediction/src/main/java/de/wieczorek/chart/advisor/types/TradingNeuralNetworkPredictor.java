package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.nn.AbstractNeuralNetworkPredictor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class TradingNeuralNetworkPredictor
        extends AbstractNeuralNetworkPredictor<NetInputItem, TradingEvaluationResult> {

    @Override
    protected INDArray buildPredictionFeatures(NetInputItem item) {
        int maxLength = 4 * 60 + 1;
        int vectorSize = 20;

        double[][] itemVectors = new double[vectorSize][maxLength];
        int index = 0;
        INDArray features = Nd4j.create(new int[]{1, vectorSize, maxLength}, 'f');

        Map<ChartEntry, List<ChartMetricRecord>> allRecords = item.getInputChartMetrics();
        List<ChartEntry> ce = new ArrayList<>(allRecords.keySet());

        ce.sort(Comparator.comparing(ChartEntry::getDate));
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
        final INDArray vectors = Nd4j.create(itemVectors);

        features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(),
                NDArrayIndex.interval(0, maxLength)}, vectors);
        return features;
    }

    @Override
    protected TradingEvaluationResult buildPredictionResult(NetInputItem input, INDArray output) {
        TradingEvaluationResult result = new TradingEvaluationResult();
        result.setPrediction(output.getDouble(0));
        return result;
    }

}
