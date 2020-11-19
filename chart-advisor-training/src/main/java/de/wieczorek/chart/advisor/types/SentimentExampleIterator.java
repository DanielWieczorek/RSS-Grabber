package de.wieczorek.chart.advisor.types;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class SentimentExampleIterator implements DataSetIterator {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final int batchSize;
    private final List<TrainingNetInputItem> files;
    private int cursor = 0;

    public SentimentExampleIterator(List<TrainingNetInputItem> entries, int batchSize, boolean train) {
        this.batchSize = batchSize;

        List<TrainingNetInputItem> positiveFiles = entries.stream().filter((item) -> item.getOutput() > 0)
                .collect(Collectors.toList());
        List<TrainingNetInputItem> negativeFiles = entries.stream().filter((item) -> item.getOutput() <= 0)
                .collect(Collectors.toList());

        files = new ArrayList<>();

        for (int i = 0; i < Math.max(positiveFiles.size(), negativeFiles.size()); i++) {
            if (i < positiveFiles.size()) {
                files.add(positiveFiles.get(i));
            }
            if (i < negativeFiles.size()) {
                files.add(negativeFiles.get(i));
            }
        }

    }

    @Override
    public DataSet next(int num) {
        if (cursor >= files.size())
            throw new NoSuchElementException();

        return nextDataSet(num);
    }

    private DataSet nextDataSet(int num) {
        // First: load reviews to String. Alternate positive and negative reviews
        List<TrainingNetInputItem> reviews = new ArrayList<>(num);
        for (int i = 0; i < num && cursor < totalExamples(); i++) {
            int posReviewNumber = cursor;

            reviews.add(files.get(posReviewNumber));

            cursor++;
        }

        int maxLength = NetworkInputBuilder.MAX_LENGTH;
        int vectorSize = NetworkInputBuilder.VECTOR_SIZE;

        // Create data for training
        // Here: we have reviews.size() examples of varying lengths
        INDArray features = Nd4j.create(new int[]{reviews.size(), vectorSize, maxLength}, 'f');
        INDArray labels = Nd4j.create(new int[]{reviews.size(), 1, maxLength}, 'f'); // Two labels: positive or

        INDArray featuresMask = Nd4j.zeros(reviews.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(reviews.size(), maxLength);

        for (int i = 0; i < reviews.size(); i++) {
            TrainingNetInputItem item = reviews.get(i);
            final INDArray vectors = item.getInput();
            features.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.all(),
                    NDArrayIndex.interval(0, maxLength)}, vectors);
            featuresMask.get(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.interval(0, maxLength)})
                    .assign(1);

            labels.putScalar(new int[]{i, 0, maxLength - 1}, item.getOutput()); // Set label: [0,1] for
            labelsMask.putScalar(new int[]{i, maxLength - 1}, 1.0); // Specify that an output exists at the final
        }

        return new DataSet(features, labels, featuresMask, labelsMask);
    }

    public int totalExamples() {
        return files.size();
    }

    @Override
    public int inputColumns() {
        return NetworkInputBuilder.MAX_LENGTH;
    }

    @Override
    public int totalOutcomes() {
        return 1;
    }

    @Override
    public void reset() {
        cursor = 0;
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public List<String> getLabels() {
        return Arrays.asList("diff");
    }

    @Override
    public boolean hasNext() {
        return cursor < totalExamples();
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }

    @Override
    public void remove() {

    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException();
    }

}