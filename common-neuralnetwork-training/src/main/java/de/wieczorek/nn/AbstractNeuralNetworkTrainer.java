package de.wieczorek.nn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.nd4j.evaluation.BaseEvaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public abstract class AbstractNeuralNetworkTrainer<T, R> {

    @Inject
    private NeuralNetworkDao dao;

    public void train(List<T> trainingSet, int nEpochs) {

	if (trainingSet == null) {
	    return;
	}
	MultiLayerNetwork net = dao.readModel();
	if (net == null) {
	    net = buildNetwork();
	}
	net.init();
	net.setListeners(new PerformanceListener(1, true));

	Random random = new Random(System.currentTimeMillis());

	int testSetSize = trainingSet.size() * 20 / 100;

	for (int i = 0; i < nEpochs; i++) {

	    List<T> filteredTrainingSet = new ArrayList<>(trainingSet);
	    List<T> testSet = new ArrayList<>();

	    for (int j = 0; j < testSetSize; j++) {
		T entry = trainingSet.get(random.nextInt(filteredTrainingSet.size()));
		testSet.add(entry);
		filteredTrainingSet.remove(entry);
	    }
	    Collections.shuffle(filteredTrainingSet);

	    // DataSetIterators for training and testing respectively
	    DataSetIterator train = buildTrainingSetIterator(filteredTrainingSet);
	    DataSetIterator test = buildTestSetIterator(testSet);

	    System.out.println("Starting training");
	    net.fit(train);
	    train.reset();
	    System.out.println("Epoch " + net.getEpochCount() + " complete. Starting evaluation:");

	    // Run evaluation. This is on 25k reviews, so can take some time
	    BaseEvaluation<?> evaluation = buildEvaluation(test, net);
	    test.reset();
	    System.out.println(evaluation.stats());
	    dao.writeModel(net);
	}

	Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
    }

    protected abstract BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net);

    protected abstract DataSetIterator buildTrainingSetIterator(List<T> trainingSet);

    protected abstract DataSetIterator buildTestSetIterator(List<T> testSet);

    protected abstract MultiLayerNetwork buildNetwork();

    protected abstract int getBatchSize();

}
