package de.wieczorek.rss.advisor.types;

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
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final int batchSize;

    private int cursor = 0;
    private final List<NetInputItem> files;

    public SentimentExampleIterator(List<NetInputItem> entries, int batchSize, boolean train) {
	this.batchSize = batchSize;

	List<NetInputItem> positiveFiles = entries.stream().filter((item) -> item.getOutputDelta() > 0)
		.collect(Collectors.toList());
	List<NetInputItem> negativeFiles = entries.stream().filter((item) -> item.getOutputDelta() <= 0)
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
	List<NetInputItem> reviews = new ArrayList<>(num);
	for (int i = 0; i < num && cursor < totalExamples(); i++) {
	    int posReviewNumber = cursor;

	    reviews.add(files.get(posReviewNumber));

	    cursor++;
	}

	int maxLength = 24 * 60 + 1;
	int vectorSize = 9;

	// Create data for training
	// Here: we have reviews.size() examples of varying lengths
	INDArray features = Nd4j.create(new int[] { reviews.size(), vectorSize, maxLength }, 'f');
	INDArray labels = Nd4j.create(new int[] { reviews.size(), 1, maxLength }, 'f'); // Two labels: positive or
											// negative
	// Because we are dealing with reviews of different lengths and only one output
	// at the final time step: use padding arrays
	// Mask arrays contain 1 if data is present at that time step for that example,
	// or 0 if data is just padding
	INDArray featuresMask = Nd4j.zeros(reviews.size(), maxLength);
	INDArray labelsMask = Nd4j.zeros(reviews.size(), maxLength);

	for (int i = 0; i < reviews.size(); i++) {
	    NetInputItem item = reviews.get(i);

	    double[][] itemVectors = new double[vectorSize][maxLength];
	    int index = 0;
	    for (DeltaChartEntry entry : item.getInputChartEntry()) {
		if (entry != null) {
		    itemVectors[0][index] = entry.getOpen();
		    itemVectors[1][index] = entry.getHigh();
		    itemVectors[2][index] = entry.getLow();
		    itemVectors[3][index] = entry.getClose();
		    itemVectors[4][index] = entry.getVolume();
		    itemVectors[5][index] = entry.getVolumeWeightedAverage();
		    itemVectors[6][index] = entry.getTransactions();
		    itemVectors[7][index] = item.getInputSentiment().getPositiveProbability();
		    itemVectors[8][index] = item.getInputSentiment().getNegativeProbability();

		} else {
		    itemVectors[0][index] = 0;
		    itemVectors[1][index] = 0;
		    itemVectors[2][index] = 0;
		    itemVectors[3][index] = 0;
		    itemVectors[4][index] = 0;
		    itemVectors[5][index] = 0;
		    itemVectors[6][index] = 0;
		    itemVectors[7][index] = item.getInputSentiment().getPositiveProbability();
		    itemVectors[8][index] = item.getInputSentiment().getNegativeProbability();
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

	    labels.putScalar(new int[] { i, 0, maxLength - 1 }, item.getOutputDelta()); // Set label: [0,1] for
											// negative, [1,0] for
	    // positive
	    labelsMask.putScalar(new int[] { i, maxLength - 1 }, 1.0); // Specify that an output exists at the final
								       // time
								       // step for this example
	}

	DataSet result = new DataSet(features, labels, featuresMask, labelsMask);
	return result;
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
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
	throw new UnsupportedOperationException();
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

}