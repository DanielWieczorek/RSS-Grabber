package de.wieczorek.rss.insight.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class SentimentExampleIterator implements DataSetIterator {
    private final int batchSize;

    private int cursor = 0;
    private final List<TrainingDataItem> files;

    public SentimentExampleIterator(List<TrainingDataItem> entries, int batchSize, boolean train) {
	this.batchSize = batchSize;

	List<TrainingDataItem> positiveFiles = entries.stream().filter((item) -> item.getOutputSentiment() == 1)
		.collect(Collectors.toList());
	List<TrainingDataItem> negativeFiles = entries.stream().filter((item) -> item.getOutputSentiment() == -1)
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
	try {
	    return nextDataSet(num);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private DataSet nextDataSet(int num) throws IOException {
	// First: load reviews to String. Alternate positive and negative reviews
	List<TrainingDataItem> reviews = new ArrayList<>(num);
	boolean[] positive = new boolean[num];
	for (int i = 0; i < num && cursor < totalExamples(); i++) {
	    // Load positive review
	    int posReviewNumber = cursor;

	    reviews.add(files.get(posReviewNumber));
	    positive[i] = files.get(posReviewNumber).getOutputSentiment() == 1;

	    cursor++;
	}

	int maxLength = 24 * 60 + 1;
	int vectorSize = 9;

	// Create data for training
	// Here: we have reviews.size() examples of varying lengths
	INDArray features = Nd4j.create(new int[] { reviews.size(), vectorSize, maxLength }, 'f');
	INDArray labels = Nd4j.create(new int[] { reviews.size(), 2, maxLength }, 'f'); // Two labels: positive or
											// negative
	// Because we are dealing with reviews of different lengths and only one output
	// at the final time step: use padding arrays
	// Mask arrays contain 1 if data is present at that time step for that example,
	// or 0 if data is just padding
	INDArray featuresMask = Nd4j.zeros(reviews.size(), maxLength);
	INDArray labelsMask = Nd4j.zeros(reviews.size(), maxLength);

	for (int i = 0; i < reviews.size(); i++) {
	    TrainingDataItem item = reviews.get(i);

	    double[][] itemVectors = new double[maxLength][vectorSize];
	    int index = 0;
	    for (DeltaChartEntry entry : item.getInputChartEntry()) {
		if (entry != null) {
		    itemVectors[index][0] = entry.getOpen();
		    itemVectors[index][1] = entry.getHigh();
		    itemVectors[index][2] = entry.getLow();
		    itemVectors[index][3] = entry.getClose();
		    itemVectors[index][4] = entry.getVolume();
		    itemVectors[index][5] = entry.getVolumeWeightedAverage();
		    itemVectors[index][6] = entry.getTransactions();
		    itemVectors[index][7] = item.getInputSentiment().getPositiveProbability();
		    itemVectors[index][8] = item.getInputSentiment().getNegativeProbability();

		} else {
		    itemVectors[index][0] = 0;
		    itemVectors[index][1] = 0;
		    itemVectors[index][2] = 0;
		    itemVectors[index][3] = 0;
		    itemVectors[index][4] = 0;
		    itemVectors[index][5] = 0;
		    itemVectors[index][6] = 0;
		    itemVectors[index][7] = item.getInputSentiment().getPositiveProbability();
		    itemVectors[index][8] = item.getInputSentiment().getNegativeProbability();
		}
		index++;
	    }

	    // Get all wordvectors for the current document and transpose them to fit the
	    // 2nd and 3rd feature shape
	    final INDArray vectors = Nd4j.create(itemVectors);

	    // Put wordvectors into features array at the following indices:
	    // 1) Document (i)
	    // 2) All vector elements which is equal to NDArrayIndex.interval(0, vectorSize)
	    // 3) All elements between 0 and the length of the current sequence
	    features.put(new INDArrayIndex[] { NDArrayIndex.point(i), NDArrayIndex.all(),
		    NDArrayIndex.interval(0, maxLength) }, vectors);

	    // Assign "1" to each position where a feature is present, that is, in the
	    // interval of [0, seqLength)
	    featuresMask.get(new INDArrayIndex[] { NDArrayIndex.point(i), NDArrayIndex.interval(0, maxLength) })
		    .assign(1);

	    int idx = (positive[i] ? 0 : 1);
	    labels.putScalar(new int[] { i, idx, maxLength - 1 }, 1.0); // Set label: [0,1] for negative, [1,0] for
									// positive
	    labelsMask.putScalar(new int[] { i, maxLength - 1 }, 1.0); // Specify that an output exists at the final
								       // time
								       // step for this example
	}

	return new DataSet(features, labels, featuresMask, labelsMask);
    }

    public int totalExamples() {
	return files.size();
    }

    @Override
    public int inputColumns() {
	return 24 * 60;
    }

    @Override
    public int totalOutcomes() {
	return 2;
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
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
	throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getLabels() {
	return Arrays.asList("positive", "negative");
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

}