package de.wieczorek.chart.advisor.types;

import de.wieczorek.nn.AbstractNeuralNetworkTrainer;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
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

        int vectorSize = NetworkInputBuilder.VECTOR_SIZE;
        int secondaryLevelSize = vectorSize * 7;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(0)
                .updater(new Adam(1e-4))
//                .l2(1e-5)
                .weightInit(WeightInit.XAVIER)
//                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
//                .gradientNormalizationThreshold(1.0)
                .trainingWorkspaceMode(WorkspaceMode.ENABLED).list()
                .layer(0, new LSTM.Builder().nIn(vectorSize).nOut(secondaryLevelSize).activation(Activation.TANH).build())
                .layer(1, new LSTM.Builder().nOut(secondaryLevelSize).activation(Activation.TANH).build())
                .layer(2, new LSTM.Builder().nOut(secondaryLevelSize).activation(Activation.TANH).build())
                .layer(3, new LSTM.Builder().nOut(secondaryLevelSize).activation(Activation.TANH).build())
                .layer(4, new LSTM.Builder().nOut(secondaryLevelSize).activation(Activation.TANH).build())
                .layer(5, new DenseLayer.Builder().nOut(secondaryLevelSize).activation(Activation.LEAKYRELU).build())
                .layer(6, new DenseLayer.Builder().nOut(secondaryLevelSize).activation(Activation.LEAKYRELU).build())
                .layer(7, new BatchNormalization.Builder().nOut(secondaryLevelSize).build())

                .layer(8
                        ,
                        new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
                                .weightInit(WeightInit.XAVIER).nOut(1).build())
                .backpropType(BackpropType.Standard)
                //.tBPTTLength(64)
                .build();
        conf.setIterationCount(1);
        conf.setCacheMode(CacheMode.DEVICE);
        //conf.setDataType(DataType.HALF);
        //Nd4j.getExecutioner().setProfilingConfig(ProfilerConfig.builder().checkForNAN(true).build());
        return new MultiLayerNetwork(conf);
    }

    @Override
    protected int getBatchSize() {
        return 512;
    }

    @Override
    protected BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net) {

        return net.evaluateRegression(test);
    }
}
