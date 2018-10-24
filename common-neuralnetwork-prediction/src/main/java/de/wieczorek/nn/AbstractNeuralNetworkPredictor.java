package de.wieczorek.nn;

import javax.inject.Inject;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

public abstract class AbstractNeuralNetworkPredictor<T, R> {

    @Inject
    private NeuralNetworkDao dao;

    public R predict(T item) {
	MultiLayerNetwork net = dao.readModel();
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

}
