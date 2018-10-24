package de.wieczorek.rss.advisor.business;

import javax.enterprise.context.ApplicationScoped;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import de.wieczorek.nn.AbstractNeuralNetworkPredictor;
import de.wieczorek.rss.advisor.types.DeltaChartEntry;
import de.wieczorek.rss.advisor.types.NetInputItem;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;

@ApplicationScoped
public class TradingNeuralNetworkPredictor
	extends AbstractNeuralNetworkPredictor<NetInputItem, TradingEvaluationResult> {

    @Override
    protected INDArray buildPredictionFeatures(NetInputItem item) {
	int maxLength = 24 * 60 + 1;
	int vectorSize = 9;

	double[][] itemVectors = new double[item.getInputChartEntry().size()][vectorSize];
	int index = 0;
	INDArray features = Nd4j.create(
		new int[] { item.getInputChartEntry().size(), vectorSize, item.getInputChartEntry().size() }, 'f');

	for (DeltaChartEntry entry : item.getInputChartEntry()) {
	    if (entry != null) {
		itemVectors[index][0] = entry.getOpen();
		itemVectors[index][1] = entry.getHigh();
		itemVectors[index][2] = entry.getLow();
		itemVectors[index][3] = entry.getClose();
		itemVectors[index][4] = entry.getVolume();
		itemVectors[index][5] = entry.getVolumeWeightedAverage();
		itemVectors[index][6] = entry.getTransactions();
		itemVectors[index][7] = item.getInputSentiment().getPositiveProbability();
		itemVectors[index][8] = item.getInputSentiment().getNegativeProbability();

	    } else {
		itemVectors[index][0] = 0;
		itemVectors[index][1] = 0;
		itemVectors[index][2] = 0;
		itemVectors[index][3] = 0;
		itemVectors[index][4] = 0;
		itemVectors[index][5] = 0;
		itemVectors[index][6] = 0;
		itemVectors[index][7] = item.getInputSentiment().getPositiveProbability();
		itemVectors[index][8] = item.getInputSentiment().getNegativeProbability();
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
