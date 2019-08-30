package de.wieczorek.nn;

import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class PolicyDao {

    @Inject
    @NeuralNetworkName
    private String fileName;

    public void writePolicy(DQNPolicy<?> policy) {
        try {
            policy.save(buildPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePolicy(ACPolicy<?> policy) {
        try {
            policy.save(buildPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildPath() {
        return System.getProperty("user.home") + "/neural-networks/" + fileName;
    }

    public DQNPolicy<?> readPolicy() {
        try {
            return DQNPolicy.load(buildPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
