package de.wieczorek.chart.advisor.types;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DropoutLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.evaluation.BaseEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import de.wieczorek.nn.AbstractNeuralNetworkTrainer;

@ApplicationScoped
public class TradingNeuralNetworkTrainer extends AbstractNeuralNetworkTrainer<TrainingNetInputItem, TradingEvaluationResult> {

    @Override
    protected DataSetIterator buildTrainingSetIterator(List<TrainingNetInputItem> trainingSet) {


        SentimentExampleIterator train = new SentimentExampleIterator(trainingSet, getBatchSize(), true);
        return train;
    }

    @Override
    protected DataSetIterator buildTestSetIterator(List<TrainingNetInputItem> testSet) {
        SentimentExampleIterator test = new SentimentExampleIterator(testSet, getBatchSize(), false);

        return test;
    }

    @Override
    protected MultiLayerNetwork buildNetwork() {

        int vectorSize = 20;
        final int seed = 0;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).updater(new Adam(2e-4)).l2(1e-5)
                .weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.ENABLED).list()
                .layer(0, new LSTM.Builder().nIn(vectorSize).nOut(120).activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nOut(120).activation(Activation.TANH).build())
                .layer(2, new LSTM.Builder().nOut(120).activation(Activation.TANH).build())
                .layer(3, new LSTM.Builder().nOut(120).activation(Activation.TANH).build())
                .layer(4, new DenseLayer.Builder().nOut(120).activation(Activation.RELU).build())
                .layer(5, new DropoutLayer.Builder().nOut(120).build())
                .layer(6,
                        new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
                                .l2(0.0001).weightInit(WeightInit.XAVIER).nOut(1).build())

                .backpropType(BackpropType.Standard)

                .build();
        conf.setIterationCount(10);

        MultiLayerNetwork net = new MultiLayerNetwork(conf);

        return net;
    }

    @Override
    protected int getBatchSize() {
        return 256;
    }

    @Override
    protected BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net) {

        return net.evaluateRegression(test);
    }
}
