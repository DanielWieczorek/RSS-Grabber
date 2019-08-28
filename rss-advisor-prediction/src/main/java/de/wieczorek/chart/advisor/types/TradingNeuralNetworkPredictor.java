package de.wieczorek.chart.advisor.types;

import javax.enterprise.context.ApplicationScoped;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import de.wieczorek.nn.AbstractNeuralNetworkPredictor;

@ApplicationScoped
public class TradingNeuralNetworkPredictor
	extends AbstractNeuralNetworkPredictor<NetInputItem, TradingEvaluationResult> {

    @Override
    protected INDArray buildPredictionFeatures(NetInputItem item) {
	int maxLength = 24 * 60 + 1;
	int vectorSize = 9;

	double[][] itemVectors = new double[vectorSize][item.getInputChartEntry().size()];
	int index = 0;
	INDArray features = Nd4j.create(new int[] { 1, vectorSize, item.getInputChartEntry().size() }, 'f');

	for (DeltaChartEntry entry : item.getInputChartEntry()) {
	    if (entry != null) {
		itemVectors[0][index] = entry.getOpen();
		itemVectors[1][index] = entry.getHigh();
		itemVectors[2][index] = entry.getLow();
		itemVectors[3][index] = entry.getClose();
		itemVectors[4][index] = entry.getVolume();
		itemVectors[5][index] = entry.getVolumeWeightedAverage();
		itemVectors[6][index] = entry.getTransactions();
		itemVectors[7][index] = item.getInputSentiment().getPositiveProbability();
		itemVectors[8][index] = item.getInputSentiment().getNegativeProbability();

	    } else {
		itemVectors[0][index] = 0;
		itemVectors[1][index] = 0;
		itemVectors[2][index] = 0;
		itemVectors[3][index] = 0;
		itemVectors[4][index] = 0;
		itemVectors[5][index] = 0;
		itemVectors[6][index] = 0;
		itemVectors[7][index] = item.getInputSentiment().getPositiveProbability();
		itemVectors[8][index] = item.getInputSentiment().getNegativeProbability();
	    }
	    index++;

	}
	final INDArray vectors = Nd4j.create(itemVectors);

	features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(),
		NDArrayIndex.interval(0, item.getInputChartEntry().size()) }, vectors);
	return features;
    }

    @Override
    protected TradingEvaluationResult buildPredictionResult(NetInputItem input, INDArray output) {
	TradingEvaluationResult result = new TradingEvaluationResult();
	result.setPredictedDelta(output.getDouble(0));
	return result;
    }

}
