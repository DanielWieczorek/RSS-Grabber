package de.wieczorek.chart.advisor.types;

import de.wieczorek.nn.AbstractNeuralNetworkTrainer;
import org.deeplearning4j.nn.conf.*;
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

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TradingNeuralNetworkTrainer extends AbstractNeuralNetworkTrainer<TrainingNetInputItem> {

    @Override
    protected DataSetIterator buildTrainingSetIterator(List<TrainingNetInputItem> trainingSet) {
        return new SentimentExampleIterator(trainingSet, getBatchSize(), true);
    }

    @Override
    protected DataSetIterator buildTestSetIterator(List<TrainingNetInputItem> testSet) {
        return new SentimentExampleIterator(testSet, getBatchSize(), false);
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

        return new MultiLayerNetwork(conf);
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
