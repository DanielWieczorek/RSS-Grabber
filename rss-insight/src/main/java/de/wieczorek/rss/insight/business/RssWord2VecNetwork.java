package de.wieczorek.rss.insight.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.reader.ModelUtils;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;

@ApplicationScoped
public class RssWord2VecNetwork implements WordVectors {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Word2Vec vec;

    public void train(List<RssEntry> findAllClassified) {
	SentenceIterator iter = new Word2VecExampleIterator(findAllClassified);
	TokenizerFactory t = new DefaultTokenizerFactory();
	t.setTokenPreProcessor(new CommonPreprocessor());

	Word2Vec vec = new Word2Vec.Builder().minWordFrequency(5).iterations(1).layerSize(100).seed(42).windowSize(5)
		.iterate(iter).tokenizerFactory(t).build();

	vec.fit();
	this.vec = vec;
    }

    @Override
    public String getUNK() {
	return vec.getUNK();
    }

    @Override
    public void setUNK(String newUNK) {
	vec.setUNK(newUNK);
    }

    @Override
    public boolean hasWord(String word) {
	return vec.hasWord(word);
    }

    @Override
    public Collection<String> wordsNearest(INDArray words, int top) {
	return vec.wordsNearest(words, top);
    }

    @Override
    public Collection<String> wordsNearestSum(INDArray words, int top) {
	return vec.wordsNearest(words, top);
    }

    @Override
    public Collection<String> wordsNearestSum(String word, int n) {
	return vec.wordsNearestSum(word, n);
    }

    @Override
    public Collection<String> wordsNearestSum(Collection<String> positive, Collection<String> negative, int top) {
	return vec.wordsNearestSum(positive, negative, top);
    }

    @Override
    public Map<String, Double> accuracy(List<String> questions) {
	return vec.accuracy(questions);
    }

    @Override
    public int indexOf(String word) {
	return vec.indexOf(word);
    }

    @Override
    public List<String> similarWordsInVocabTo(String word, double accuracy) {
	return vec.similarWordsInVocabTo(word, accuracy);
    }

    @Override
    public double[] getWordVector(String word) {
	return vec.getWordVector(word);
    }

    @Override
    public INDArray getWordVectorMatrixNormalized(String word) {
	return vec.getWordVectorMatrixNormalized(word);
    }

    @Override
    public INDArray getWordVectorMatrix(String word) {
	return vec.getWordVectorMatrix(word);
    }

    @Override
    public INDArray getWordVectors(Collection<String> labels) {
	return vec.getWordVectors(labels);
    }

    @Override
    public INDArray getWordVectorsMean(Collection<String> labels) {
	return vec.getWordVectorsMean(labels);
    }

    @Override
    public Collection<String> wordsNearest(Collection<String> positive, Collection<String> negative, int top) {
	return vec.wordsNearest(positive, negative, top);
    }

    @Override
    public Collection<String> wordsNearest(String word, int n) {
	return vec.wordsNearest(word, n);
    }

    @Override
    public double similarity(String word, String word2) {
	return vec.similarity(word, word2);
    }

    @Override
    public VocabCache vocab() {
	return vec.vocab();
    }

    @Override
    public WeightLookupTable lookupTable() {
	return vec.lookupTable();
    }

    @Override
    public void setModelUtils(ModelUtils utils) {
	vec.setModelUtils(utils);

    }

    public int getLayerSize() {
	return vec.getLayerSize();
    }

}
