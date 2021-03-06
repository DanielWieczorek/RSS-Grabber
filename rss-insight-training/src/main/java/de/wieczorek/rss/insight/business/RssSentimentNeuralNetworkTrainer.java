package de.wieczorek.rss.insight.business;

import de.wieczorek.nn.AbstractNeuralNetworkTrainer;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.BaseEvaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RssSentimentNeuralNetworkTrainer extends AbstractNeuralNetworkTrainer<ClassifiedRssEntry> {

    @Inject
    private RssWord2VecNetwork vec;
    private int truncateReviewsToLength = 128;

    @Override
    protected DataSetIterator buildTrainingSetIterator(List<ClassifiedRssEntry> trainingSet) {
        WordVectors wordVectors = vec;
        return new SentimentExampleIterator(trainingSet, wordVectors, getBatchSize(),
                truncateReviewsToLength, true);
    }

    @Override
    protected DataSetIterator buildTestSetIterator(List<ClassifiedRssEntry> testSet) {
        WordVectors wordVectors = vec;
        return new SentimentExampleIterator(testSet, wordVectors, getBatchSize(),
                truncateReviewsToLength, false);
    }

    @Override
    protected MultiLayerNetwork buildNetwork() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(0).updater(new Adam(2e-2)).l2(1e-5)
                .weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.ENABLED)
                .inferenceWorkspaceMode(WorkspaceMode.ENABLED).list()
                .layer(0, new LSTM.Builder().nIn(vec.getLayerSize()).nOut(512).activation(Activation.TANH).build())
                .layer(1,
                        new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                                .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(512).nOut(2).build()).backpropType(BackpropType.Standard)
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

        return net;
    }

    @Override
    protected int getBatchSize() {
        return 64;
    }

    @Override
    protected BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net) {
        return net.evaluate(test);
    }

}
