package de.wieczorek.rss.insight.business;

import java.util.List;

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
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

@ApplicationScoped
public class RssSentimentNeuralNetwork {

    @Inject
    private RssWord2VecNetwork vec;

    public void train(List<RssEntry> findAllClassified) {
	vec.train(findAllClassified);
	int batchSize = 64; // Number of examples in each minibatch
	int vectorSize = vec.getLayerSize(); // Size of the word vectors. 300 in the Google News model
	int nEpochs = 100; // Number of epochs (full passes of training data) to train on
	int truncateReviewsToLength = 256; // Truncate reviews with length (# words) greater than this
	final int seed = 0; // Seed for reproducibility

	Nd4j.getMemoryManager().setAutoGcWindow(10000); // https://deeplearning4j.org/workspaces

	// Set up network configuration
	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new Adam(2e-2)).l2(1e-5)
		.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
		.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SEPARATE)
		.inferenceWorkspaceMode(WorkspaceMode.SEPARATE) // https://deeplearning4j.org/workspaces
		.list().layer(0, new LSTM.Builder().nIn(vectorSize).nOut(256).activation(Activation.TANH).build())
		.layer(1,
			new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
				.lossFunction(LossFunctions.LossFunction.MCXENT).nIn(256).nOut(2).build())
		.pretrain(false).backprop(true).build();

	MultiLayerNetwork net = new MultiLayerNetwork(conf);
	net.init();
	net.setListeners(new ScoreIterationListener(1));

	// DataSetIterators for training and testing respectively
	WordVectors wordVectors = vec;
	SentimentExampleIterator train = new SentimentExampleIterator(findAllClassified, wordVectors, batchSize,
		truncateReviewsToLength, true);
	SentimentExampleIterator test = new SentimentExampleIterator(findAllClassified, wordVectors, batchSize,
		truncateReviewsToLength, false);

	System.out.println("Starting training");
	for (int i = 0; i < nEpochs; i++) {
	    net.fit(train);
	    train.reset();
	    System.out.println("Epoch " + i + " complete. Starting evaluation:");

	    // Run evaluation. This is on 25k reviews, so can take some time
	    Evaluation evaluation = net.evaluate(test);
	    System.out.println(evaluation.stats());
	}

    }

}
