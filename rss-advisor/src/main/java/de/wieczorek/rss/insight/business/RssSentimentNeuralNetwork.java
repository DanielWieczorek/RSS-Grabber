package de.wieczorek.rss.insight.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import org.deeplearning4j.eval.Evaluation;
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
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

@ApplicationScoped
public class RssSentimentNeuralNetwork {

    private MultiLayerNetwork net;

    public void train(List<TrainingDataItem> findAllClassified) {
	if (findAllClassified == null) {
	    return;
	}
	int batchSize = 128; // Number of examples in each minibatch
	int vectorSize = 9; // Size of the word vectors. 300 in the Google News model
	int nEpochs = 25; // Number of epochs (full passes of training data) to train on
	int truncateReviewsToLength = 128; // Truncate reviews with length (# words) greater than this
	final int seed = 0; // Seed for reproducibility

	// Nd4j.getMemoryManager().setAutoGcWindow(20000); //
	// https://deeplearning4j.org/workspaces
	Nd4j.getMemoryManager().togglePeriodicGc(false);
	// Set up network configuration
	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new Adam(2e-2)).l2(1e-5)
		.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
		.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.ENABLED)
		.inferenceWorkspaceMode(WorkspaceMode.ENABLED) // https://deeplearning4j.org/workspaces
		.list().layer(0, new LSTM.Builder().nIn(vectorSize).nOut(128).activation(Activation.TANH).build())
		.layer(1,
			new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
				.lossFunction(LossFunctions.LossFunction.MCXENT).nIn(128).nOut(2).build())
		.pretrain(false).backprop(true).build();

	net = new MultiLayerNetwork(conf);
	net.init();
	net.setListeners(new PerformanceListener(1, true));

	Random random = new Random(System.currentTimeMillis());

	int trainingSetSize = findAllClassified.size() * 20 / 100;

	for (int i = 0; i < nEpochs; i++) {

	    List<TrainingDataItem> trainingSet = new ArrayList<>(findAllClassified);
	    List<TrainingDataItem> testSet = new ArrayList<>();

	    for (int j = 0; j < trainingSetSize; j++) {
		TrainingDataItem entry = trainingSet.get(random.nextInt(trainingSet.size()));
		testSet.add(entry);
		trainingSet.remove(entry);
	    }

	    // DataSetIterators for training and testing respectively
	    SentimentExampleIterator train = new SentimentExampleIterator(trainingSet, batchSize, true);
	    SentimentExampleIterator test = new SentimentExampleIterator(testSet, batchSize, false);

	    System.out.println("Starting training");

	    net.fit(train);
	    train.reset();
	    System.out.println("Epoch " + i + " complete. Starting evaluation:");

	    // Run evaluation. This is on 25k reviews, so can take some time
	    Evaluation evaluation = net.evaluate(test);
	    test.reset();
	    System.out.println(evaluation.stats());
	}
    }

    public EvaluationResult predict(TrainingDataItem item) {
	if (item == null) {
	    EvaluationResult result = new EvaluationResult();
	    result.setPositiveProbability(0.5);
	    result.setNegativeProbability(0.5);
	    return result;
	}
	if (net != null) {
	    net.clear();
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

	    features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(),
		    NDArrayIndex.interval(0, maxLength) }, vectors);
	    // Get all wordvectors for the current document and transpose them to fit the
	    // 2nd and 3rd feature shape

	    INDArray outputRaw = net.output(features);
	    long timeSeriesLength = outputRaw.size(2);
	    INDArray probabilitiesAtLastWord = outputRaw.get(NDArrayIndex.point(0), NDArrayIndex.all(),
		    NDArrayIndex.point(timeSeriesLength - 1));
	    System.out.println(probabilitiesAtLastWord);

	    EvaluationResult result = new EvaluationResult();
	    result.setPositiveProbability(probabilitiesAtLastWord.getDouble(0));
	    result.setNegativeProbability(probabilitiesAtLastWord.getDouble(1));

	    return result;
	}
	return null;
    }
}
