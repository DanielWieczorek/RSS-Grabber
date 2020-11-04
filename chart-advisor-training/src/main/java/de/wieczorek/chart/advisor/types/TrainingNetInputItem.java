package de.wieczorek.chart.advisor.types;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TrainingNetInputItem {

    private double[][] input;
    private double output;

    public TrainingNetInputItem(double[][] input, double output) {
        this.input = input;
        this.output = output;
    }

    public double getOutput() {
        return output;
    }

    public INDArray getInput() {
        return Nd4j.create(input);
    }
}
