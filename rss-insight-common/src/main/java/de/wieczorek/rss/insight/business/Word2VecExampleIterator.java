package de.wieczorek.rss.insight.business;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import de.wieczorek.rss.classification.types.RssEntry;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;

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

    /**
     * @param dataDirectory  the directory of the IMDB review data set
     * @param wordVectors    WordVectors object
     * @param batchSize      Size of each minibatch for training
     * @param truncateLength If reviews exceed
     * @param train          If true: return the training data. If false: return the testing
     *                       data.
     */
    public Word2VecExampleIterator(List<RssEntry> entry) {
        entries = entry.stream().map(x -> x.getHeading() + ". " + x.getDescription()).collect(Collectors.toList());
        iter = entries.iterator();

    }

    @Override
    public String nextSentence() {
        Stemmer stemmer = new PorterStemmer();

        return Arrays.asList(iter.next().split(" ")).stream().map(stemmer::stem).collect(Collectors.joining(" "));
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