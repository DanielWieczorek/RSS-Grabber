package de.wieczorek.nn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.deeplearning4j.eval.BaseEvaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

public abstract class AbstractNeuralNetwork<T, R> {

    private ReentrantLock lock = new ReentrantLock();

    public void train(List<T> trainingSet, int nEpochs) {

	if (trainingSet == null) {
	    return;
	}
	MultiLayerNetwork net = readModel();
	if (net == null) {
	    net = buildNetwork();
	}
	net.init();
	net.setListeners(new PerformanceListener(1, true));

	Random random = new Random(System.currentTimeMillis());

	int trainingSetSize = trainingSet.size() * 20 / 100;

	for (int i = 0; i < nEpochs; i++) {

	    List<T> filteredTrainingSet = new ArrayList<>(trainingSet);
	    List<T> testSet = new ArrayList<>();

	    for (int j = 0; j < trainingSetSize; j++) {
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
	}
	writeModel(net);
	Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
    }

    protected abstract BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net);

    private void writeModel(MultiLayerNetwork net) {
	lock.lock();
	try {
	    ModelSerializer.writeModel(net, getFileName(), false);

	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    lock.unlock();
	}
    }

    protected MultiLayerNetwork readModel() {
	lock.lock();
	try {
	    return ModelSerializer.restoreMultiLayerNetwork(getFileName());
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    lock.unlock();
	}
	return null;
    }

    protected abstract File getFileName();

    protected abstract DataSetIterator buildTrainingSetIterator(List<T> trainingSet);

    protected abstract DataSetIterator buildTestSetIterator(List<T> testSet);

    public R predict(T item) {
	MultiLayerNetwork net = readModel();
	if (net == null) {
	    throw new RuntimeException(); // TODO
	}
	INDArray outputRaw = net.output(buildPredictionFeatures(item));
	long timeSeriesLength = outputRaw.size(2); // TODO
	INDArray probabilitiesAtLastWord = outputRaw.get(NDArrayIndex.point(0), NDArrayIndex.all(),
		NDArrayIndex.point(timeSeriesLength - 1));
	Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
	return buildPredictionResult(item, probabilitiesAtLastWord);

    }

    protected abstract INDArray buildPredictionFeatures(T item);

    protected abstract R buildPredictionResult(T input, INDArray output);

    protected abstract MultiLayerNetwork buildNetwork();

    protected abstract int getBatchSize();

}
