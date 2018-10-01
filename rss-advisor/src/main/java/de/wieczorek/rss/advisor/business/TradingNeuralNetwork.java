package de.wieczorek.rss.advisor.business;

import java.io.File;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.deeplearning4j.eval.BaseEvaluation;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import de.wieczorek.nn.AbstractNeuralNetwork;
import de.wieczorek.rss.advisor.persistence.TradingEvaluationResult;

@ApplicationScoped
public class TradingNeuralNetwork extends AbstractNeuralNetwork<NetInputItem, TradingEvaluationResult> {

    @Override
    protected File getFileName() {
	return new File("rss-advisor-TradingNeuralNetwork");
    }

    @Override
    protected DataSetIterator buildTrainingSetIterator(List<NetInputItem> trainingSet) {
	SentimentExampleIterator train = new SentimentExampleIterator(trainingSet, getBatchSize(), true);
	return train;
    }

    @Override
    protected DataSetIterator buildTestSetIterator(List<NetInputItem> testSet) {
	SentimentExampleIterator test = new SentimentExampleIterator(testSet, getBatchSize(), false);

	return test;
    }

    @Override
    protected INDArray buildPredictionFeatures(NetInputItem item) {
	int maxLength = 24 * 60 + 1;
	int vectorSize = 9;

	double[][] itemVectors = new double[item.getInputChartEntry().size()][vectorSize];
	int index = 0;
	INDArray features = Nd4j.create(new int[] { item.getInputChartEntry().size(), vectorSize, maxLength }, 'f');

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

	features.put(
		new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.interval(0, maxLength) },
		vectors);
	return features;
    }

    @Override
    protected TradingEvaluationResult buildPredictionResult(NetInputItem input, INDArray output) {
	TradingEvaluationResult result = new TradingEvaluationResult();
	result.setPredictedDelta(output.getDouble(0));
	return result;
    }

    @Override
    protected MultiLayerNetwork buildNetwork() {
	int vectorSize = 9; // Size of the word vectors. 300 in the Google News model
	final int seed = 0; // Seed for reproducibility
	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new Adam(2e-2)).l2(1e-5)
		.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
		.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.ENABLED)
		.inferenceWorkspaceMode(WorkspaceMode.ENABLED) // https://deeplearning4j.org/workspaces
		.list().layer(0, new LSTM.Builder().nIn(vectorSize).nOut(128).activation(Activation.TANH).build())
		.layer(1,
			new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
				.l2(0.0001).weightInit(WeightInit.XAVIER).nIn(128).nOut(1).build())
		.pretrain(false).backprop(true).build();

	MultiLayerNetwork net = new MultiLayerNetwork(conf);
	net.init();
	net.setListeners(new PerformanceListener(1, true));
	return net;
    }

    @Override
    protected int getBatchSize() {
	return 128;
    }

    @Override
    protected BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net) {
	return net.evaluateRegression(test);
    }
}
