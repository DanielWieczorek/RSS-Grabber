package de.wieczorek.nn;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.deeplearning4j.parallelism.ParallelInference;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import javax.inject.Inject;
import java.io.File;

public abstract class AbstractNeuralNetworkPredictor<T, R> {

    ParallelInference wrapped;
    @Inject
    private NeuralNetworkPathBuilder pathBuilder;

    public R predict(T item) {
        MultiLayerNetwork net = readNetwork();

        if (net == null) {
            throw new RuntimeException(); // TODO
        }
        if (wrapped == null) {
            wrapped = new ParallelInference.Builder(net).build();
        } else {
            wrapped.updateModel(net);
        }

        net.setCacheMode(null);
        Nd4j.getMemoryManager().setAutoGcWindow(500);
        INDArray outputRaw = net.output(buildPredictionFeatures(item));
        long timeSeriesLength = outputRaw.size(2); // TODO
        INDArray probabilitiesAtLastWord = outputRaw.get(NDArrayIndex.point(0), NDArrayIndex.all(),
                NDArrayIndex.point(timeSeriesLength - 1));
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();
        return buildPredictionResult(item, probabilitiesAtLastWord);

    }

    protected abstract INDArray buildPredictionFeatures(T item);

    protected abstract R buildPredictionResult(T input, INDArray output);

    private MultiLayerNetwork readNetwork() {
        File dir = pathBuilder.getCheckpointsPath();
        MultiLayerNetwork net = null;
        File[] checkpointFiles = dir.listFiles();
        if (checkpointFiles != null
                && checkpointFiles.length > 0
                && CheckpointListener.lastCheckpoint(dir) != null) {
            net = CheckpointListener.loadCheckpointMLN(dir, CheckpointListener.lastCheckpoint(dir));
        }
        return net;
    }


}
