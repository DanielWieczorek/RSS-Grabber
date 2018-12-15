package de.wieczorek.rss.advisor.business;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.deeplearning4j.eval.BaseEvaluation;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import de.wieczorek.nn.AbstractNeuralNetworkTrainer;
import de.wieczorek.rss.advisor.types.NetInputItem;
import de.wieczorek.rss.advisor.types.TradingEvaluationResult;

@ApplicationScoped
public class TradingNeuralNetworkTrainer extends AbstractNeuralNetworkTrainer<NetInputItem, TradingEvaluationResult> {

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
    protected MultiLayerNetwork buildNetwork() {

	int vectorSize = 9; // Size of the word vectors. 300 in the Google News model
	final int seed = 0; // Seed for reproducibility

	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new Adam(2e-2)).l2(1e-5)
		.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
		.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.ENABLED)
		.inferenceWorkspaceMode(WorkspaceMode.ENABLED).list()
		.layer(0, new LSTM.Builder().nIn(vectorSize).nOut(128).activation(Activation.TANH).build())
		.layer(1, new LSTM.Builder().nOut(512).activation(Activation.TANH).build())
		.layer(2, new DenseLayer.Builder().nOut(512).activation(Activation.RELU).build())
		.layer(3,
			new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
				.l2(0.0001).weightInit(WeightInit.XAVIER).nOut(1).build())
		.pretrain(false).backprop(true).backpropType(BackpropType.TruncatedBPTT).tBPTTBackwardLength(60)
		.tBPTTForwardLength(60).build();

	MultiLayerNetwork net = new MultiLayerNetwork(conf);
	net.setListeners(new PerformanceListener(1, true));
	net.setCacheMode(CacheMode.DEVICE);
	net.init();

	return net;
    }

    @Override
    protected int getBatchSize() {
	return 256;
    }

    @Override
    protected BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net) {
	System.out.println(net.memoryInfo(getBatchSize(), InputType.recurrent(9, 1441)));

	return net.evaluateRegression(test);
    }
}
