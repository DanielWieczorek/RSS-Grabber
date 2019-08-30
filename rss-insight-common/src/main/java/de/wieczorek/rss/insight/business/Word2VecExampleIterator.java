package de.wieczorek.rss.insight.business;

import de.wieczorek.rss.classification.types.RssEntry;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a DataSetIterator that is specialized for the IMDB review dataset
 * used in the Word2VecSentimentRNN example It takes either the train or test
 * set data from this data set, plus a WordVectors object (typically the Google
 * News 300 pretrained vectors from https://code.google.com/p/word2vec/) and
 * generates training data sets.<br>
 * Inputs/features: variable-length time series, where each word (with unknown
 * words removed) is represented by its Word2Vec vector representation.<br>
 * Labels/target: a single class (negative or positive), predicted at the final
 * time step (word) of each review
 *
 * @author Alex Black
 */
public class Word2VecExampleIterator implements SentenceIterator {

    private List<String> entries;
    private Iterator<String> iter;

    public Word2VecExampleIterator(List<RssEntry> entry) {
        entries = entry.stream().map(x -> x.getHeading() + ". " + x.getDescription()).collect(Collectors.toList());
        iter = entries.iterator();

    }

    @Override
    public String nextSentence() {
        Stemmer stemmer = new PorterStemmer();

        return Arrays.stream(iter.next().split(" ")).map(stemmer::stem).collect(Collectors.joining(" "));
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public void reset() {
        iter = entries.iterator();
    }

    @Override
    public void finish() {

    }

    @Override
    public SentencePreProcessor getPreProcessor() {

        return null;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {

    }

}