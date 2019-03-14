package de.wieczorek.rss.insight.business;

import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import de.wieczorek.nn.NeuralNetworkName;

@ApplicationScoped
public class Word2VecDao {

    private ReentrantLock lock = new ReentrantLock();

    @Inject
    @NeuralNetworkName
    private String fileName;

    public void writeWord2Vec(Word2Vec net) {
	lock.lock();
	try {
	    WordVectorSerializer.writeWord2VecModel(net, buildPath());
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    lock.unlock();
	}
    }

    private String buildPath() {
	return System.getProperty("user.home") + "/neural-networks/" + fileName + "-word2vec";
    }

    public Word2Vec readWord2Vec() {
	lock.lock();
	try {
	    return WordVectorSerializer.readWord2VecModel(buildPath());
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    lock.unlock();
	}
	return null;
    }

}
