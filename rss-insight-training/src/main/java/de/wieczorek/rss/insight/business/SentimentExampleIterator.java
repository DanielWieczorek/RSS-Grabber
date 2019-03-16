package de.wieczorek.rss.insight.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import de.wieczorek.rss.classification.types.RssEntry;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;

public class SentimentExampleIterator implements DataSetIterator {
    private final WordVectors wordVectors;
    private final int batchSize;
    private final int vectorSize;
    private final int truncateLength;

    private int cursor = 0;
    private final List<RssEntry> files;
    private final TokenizerFactory tokenizerFactory;

    public SentimentExampleIterator(List<RssEntry> entries, WordVectors wordVectors, int batchSize, int truncateLength,
	    boolean train) {
	this.batchSize = batchSize;
	this.vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;

	List<RssEntry> positiveFiles = entries.stream().filter((item) -> item.getClassification() == 1)
		.collect(Collectors.toList());
	List<RssEntry> negativeFiles = entries.stream().filter((item) -> item.getClassification() == -1)
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
	this.wordVectors = wordVectors;
	this.truncateLength = truncateLength;

	tokenizerFactory = new DefaultTokenizerFactory();
	tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
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
	List<String> reviews = new ArrayList<>(num);
	boolean[] positive = new boolean[num];
	for (int i = 0; i < num && cursor < files.size(); i++) {
	    // Load positive review
	    int posReviewNumber = cursor;
	    String review = files.get(posReviewNumber).getHeading() + ". "
		    + files.get(posReviewNumber).getDescription();
	    reviews.add(review);
	    positive[i] = files.get(posReviewNumber).getClassification() == 1;

	    cursor++;
	}

	// Second: tokenize reviews and filter out unknown words
	List<List<String>> allTokens = new ArrayList<>(reviews.size());
	List<String> allReviews = new ArrayList<>(reviews.size());
	int maxLength = 0;
	for (String s : reviews) {
	    List<String> tokens = tokenizerFactory.create(s).getTokens();
	    Stemmer stemmer = new PorterStemmer();
	    tokens = tokens.stream().map(stemmer::stem).map(CharSequence::toString).collect(Collectors.toList());

	    List<String> tokensFiltered = new ArrayList<>();
	    for (String t : tokens) {
		if (wordVectors.hasWord(t))
		    tokensFiltered.add(t);
	    }
	    if (!tokensFiltered.isEmpty()) {
		allTokens.add(tokensFiltered);
		allReviews.add(s);
	    }
	    maxLength = Math.max(maxLength, tokensFiltered.size());
	}

	reviews = allReviews;
	// If longest review exceeds 'truncateLength': only take the first
	// 'truncateLength' words
	if (maxLength > truncateLength)
	    maxLength = truncateLength;

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
	    List<String> tokens = allTokens.get(i);

	    // Get the truncated sequence length of document (i)
	    int seqLength = Math.min(tokens.size(), maxLength);

	    // Get all wordvectors for the current document and transpose them to fit the
	    // 2nd and 3rd feature shape
	    final INDArray vectors = wordVectors.getWordVectors(tokens.subList(0, seqLength)).transpose();

	    // Put wordvectors into features array at the following indices:
	    // 1) Document (i)
	    // 2) All vector elements which is equal to NDArrayIndex.interval(0, vectorSize)
	    // 3) All elements between 0 and the length of the current sequence
	    features.put(new INDArrayIndex[] { NDArrayIndex.point(i), NDArrayIndex.all(),
		    NDArrayIndex.interval(0, seqLength) }, vectors);

	    // Assign "1" to each position where a feature is present, that is, in the
	    // interval of [0, seqLength)
	    featuresMask.get(new INDArrayIndex[] { NDArrayIndex.point(i), NDArrayIndex.interval(0, seqLength) })
		    .assign(1);

	    int idx = (positive[i] ? 0 : 1);
	    int lastIdx = Math.min(tokens.size(), maxLength);
	    labels.putScalar(new int[] { i, idx, lastIdx - 1 }, 1.0); // Set label: [0,1] for negative, [1,0] for
								      // positive
	    labelsMask.putScalar(new int[] { i, lastIdx - 1 }, 1.0); // Specify that an output exists at the final time
								     // step for this example
	}

	return new DataSet(features, labels, featuresMask, labelsMask);
    }

    @Override
    public int inputColumns() {
	return vectorSize;
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
	return cursor < files.size();
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

    /**
     * Used post training to convert a String to a features INDArray that can be
     * passed to the network output method
     *
     * @param reviewContents
     *            Contents of the review to vectorize
     * @param maxLength
     *            Maximum length (if review is longer than this: truncate to
     *            maxLength). Use Integer.MAX_VALUE to not nruncate
     * @return Features array for the given input String
     */
    public INDArray loadFeaturesFromString(String reviewContents, int maxLength) {
	List<String> tokens = tokenizerFactory.create(reviewContents).getTokens();
	List<String> tokensFiltered = new ArrayList<>();
	for (String t : tokens) {
	    if (wordVectors.hasWord(t))
		tokensFiltered.add(t);
	}
	int outputLength = Math.max(maxLength, tokensFiltered.size());

	INDArray features = Nd4j.create(1, vectorSize, outputLength);

	for (int j = 0; j < tokens.size() && j < maxLength; j++) {
	    String token = tokens.get(j);
	    INDArray vector = wordVectors.getWordVectorMatrix(token);
	    features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j) },
		    vector);
	}

	return features;
    }
}