package de.wieczorek.rss.insight.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;

@ApplicationScoped
public class RssSentimentNeuralNetwork {

    @Inject
    private RssWord2VecNetwork vec;

    private MultiLayerNetwork net;

    public void train(List<RssEntry> findAllClassified) {
	if (findAllClassified == null) {
	    return;
	}
	vec.train(findAllClassified);
	int batchSize = 128; // Number of examples in each minibatch
	int vectorSize = vec.getLayerSize(); // Size of the word vectors. 300 in the Google News model
	int nEpochs = 25; // Number of epochs (full passes of training data) to train on
	int truncateReviewsToLength = 128; // Truncate reviews with length (# words) greater than this
	final int seed = 0; // Seed for reproducibility

	Nd4j.getMemoryManager().setAutoGcWindow(10000); // https://deeplearning4j.org/workspaces

	// Set up network configuration
	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new Adam(2e-2)).l2(1e-5)
		.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
		.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.ENABLED)
		.inferenceWorkspaceMode(WorkspaceMode.ENABLED) // https://deeplearning4j.org/workspaces
		.list().layer(0, new LSTM.Builder().nIn(vectorSize).nOut(512).activation(Activation.TANH).build())
		.layer(1,
			new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
				.lossFunction(LossFunctions.LossFunction.MCXENT).nIn(512).nOut(2).build())
		.pretrain(false).backprop(true).build();

	net = new MultiLayerNetwork(conf);
	net.init();
	net.setListeners(new ScoreIterationListener(1));

	Random random = new Random(System.currentTimeMillis());

	int trainingSetSize = findAllClassified.size() * 20 / 100;

	for (int i = 0; i < nEpochs; i++) {

	    List<RssEntry> trainingSet = new ArrayList<>(findAllClassified);
	    List<RssEntry> testSet = new ArrayList<>();

	    for (int j = 0; j < trainingSetSize; j++) {
		RssEntry entry = trainingSet.get(random.nextInt(trainingSet.size()));
		testSet.add(entry);
		trainingSet.remove(entry);
	    }

	    // DataSetIterators for training and testing respectively
	    WordVectors wordVectors = vec;
	    SentimentExampleIterator train = new SentimentExampleIterator(trainingSet, wordVectors, batchSize,
		    truncateReviewsToLength, true);
	    SentimentExampleIterator test = new SentimentExampleIterator(testSet, wordVectors, batchSize,
		    truncateReviewsToLength, false);

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

    public RssEntrySentiment predict(RssEntry entry) {
	if (net != null) {
	    Stemmer stemmer = new PorterStemmer();
	    net.clear();
	    INDArray outputRaw = net.output(
		    vec.getWordVectors(Arrays.asList((entry.getHeading() + ". " + entry.getDescription()).split(" "))
			    .stream().map(stemmer::stem).map(CharSequence::toString).collect(Collectors.toList())));
	    int timeSeriesLength = outputRaw.size(2);
	    INDArray probabilitiesAtLastWord = outputRaw.get(NDArrayIndex.point(0), NDArrayIndex.all(),
		    NDArrayIndex.point(timeSeriesLength - 1));
	    RssEntrySentiment sentiment = new RssEntrySentiment();
	    sentiment.setEntry(entry);

	    sentiment.setPositiveProbability(probabilitiesAtLastWord.getDouble(0));
	    sentiment.setNegativeProbability(probabilitiesAtLastWord.getDouble(1));

	    return sentiment;
	}
	return null;
    }
}
