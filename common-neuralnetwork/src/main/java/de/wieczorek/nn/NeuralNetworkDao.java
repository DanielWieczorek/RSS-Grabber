package de.wieczorek.nn;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

@ApplicationScoped
public class NeuralNetworkDao {
    private ReentrantLock lock = new ReentrantLock();

    @Inject
    @NeuralNetworkName
    private String fileName;

    public void writeModel(MultiLayerNetwork net) {
	lock.lock();
	try {
	    ModelSerializer.writeModel(net, buildPath(), false);
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    lock.unlock();
	}
    }

    private String buildPath() {
	return System.getProperty("user.home") + "/neural-networks/" + fileName;
    }

    public MultiLayerNetwork readModel() {
	lock.lock();
	try {
	    return ModelSerializer.restoreMultiLayerNetwork(buildPath());
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    lock.unlock();
	}
	return null;
    }

}
