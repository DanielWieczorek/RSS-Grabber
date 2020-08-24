package de.wieczorek.chart.advisor.types;

import de.wieczorek.nn.AbstractNeuralNetworkPredictor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TradingNeuralNetworkPredictor
        extends AbstractNeuralNetworkPredictor<NetInputItem, TradingEvaluationResult> {

    @Override
    protected INDArray buildPredictionFeatures(NetInputItem item) {


        double[][] itemVectors = NetworkInputBuilder.getVectors(item);
        INDArray features = Nd4j.create(new int[]{1, NetworkInputBuilder.VECTOR_SIZE, NetworkInputBuilder.MAX_LENGTH}, 'f');

        final INDArray vectors = Nd4j.create(itemVectors);

        features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(),
                NDArrayIndex.interval(0, NetworkInputBuilder.MAX_LENGTH)}, vectors);
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
