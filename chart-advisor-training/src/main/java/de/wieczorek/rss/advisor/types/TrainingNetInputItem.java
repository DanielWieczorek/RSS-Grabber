package de.wieczorek.rss.advisor.types;

import org.nd4j.linalg.api.ndarray.INDArray;

public class TrainingNetInputItem {

    private INDArray input;
    private double output;

    public TrainingNetInputItem(INDArray input, double output) {
        this.input = input;
        this.output = output;
    }

    public double getOutput() {
        return output;
    }

    public INDArray getInput() {
        return input;
    }
}
