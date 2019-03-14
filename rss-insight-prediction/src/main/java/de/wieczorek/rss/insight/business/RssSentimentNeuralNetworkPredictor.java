package de.wieczorek.rss.insight.business;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import de.wieczorek.nn.AbstractNeuralNetworkPredictor;
import de.wieczorek.rss.classification.types.RssEntry;
import de.wieczorek.rss.insight.types.RssEntrySentiment;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;

@ApplicationScoped
public class RssSentimentNeuralNetworkPredictor extends AbstractNeuralNetworkPredictor<RssEntry, RssEntrySentiment> {

    @Inject
    private RssWord2VecNetwork vec;

    @Override
    protected INDArray buildPredictionFeatures(RssEntry item) {
	Stemmer stemmer = new PorterStemmer();

	List<String> stemmedSentence = Arrays.asList((item.getHeading() + ". " + item.getDescription()).split(" "))
		.stream().map(stemmer::stem).map(CharSequence::toString).map(String::toLowerCase).filter(vec::hasWord)
		.collect(Collectors.toList());

	INDArray vectors = vec.getWordVectors(stemmedSentence);

	return Nd4j.expandDims(vectors, 2);

    }

    @Override
    protected RssEntrySentiment buildPredictionResult(RssEntry input, INDArray output) {
	RssEntrySentiment sentiment = new RssEntrySentiment();
	sentiment.setEntry(input);

	sentiment.setPositiveProbability(output.getDouble(0));
	sentiment.setNegativeProbability(output.getDouble(1));
	return sentiment;
    }
}
