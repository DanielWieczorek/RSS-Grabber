package de.wieczorek.nn;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;

@ApplicationScoped
public class NeuralNetworkPathBuilder {

    @Inject
    @NeuralNetworkName
    private String fileName;

    public File getCheckpointsPath() {
        return Paths.get(buildBasePath(), "checkpoints").toFile();
    }

    public File getTrainingDataPath() {
        return Paths.get(buildBasePath(), "training-data").toFile();
    }

    public File getTestDataPath() {
        return Paths.get(buildBasePath(), "test-data").toFile();
    }

    private String buildBasePath() {
        return Paths.get(System.getProperty("user.home"), "neural-networks", fileName).toString();
    }

}
