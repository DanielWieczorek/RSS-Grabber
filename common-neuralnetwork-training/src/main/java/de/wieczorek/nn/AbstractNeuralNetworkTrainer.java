package de.wieczorek.nn;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.BaseEvaluation;
import org.nd4j.linalg.dataset.AsyncDataSetIterator;
import org.nd4j.linalg.dataset.ExistingMiniBatchDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class AbstractNeuralNetworkTrainer<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractNeuralNetworkTrainer.class);

    @Inject
    private NeuralNetworkPathBuilder pathBuilder;

    @Inject
    @NeuralNetworkName
    private String fileName;


    public void train(IDataGenerator<T> dataGenerator, int nEpochs) {

        MultiLayerNetwork net;

        logger.debug("Retrieving or constructing network");
        net = readOrBuildNetwork();

        logger.debug("Building training data if needed");
        buildTrainingDataIfNotPresent(dataGenerator);

        File checkpointTarget = pathBuilder.getCheckpointsPath();
        checkpointTarget.mkdirs();
        logger.debug("finalizing network");
        CheckpointListener checkpointListener = new CheckpointListener.Builder(checkpointTarget)
                .logSaving(true)
                .keepLast(3)
                .deleteExisting(true)
                .saveEveryEpoch()
                .build();


        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);

        net.setListeners(new PerformanceListener(10, true), checkpointListener, new StatsListener(statsStorage));
        net.setCacheMode(CacheMode.DEVICE);


        net.init();

        logger.debug("Building iterators");
        DataSetIterator existingTrainingData = new CustomDatasetIterator(pathBuilder.getTrainingDataPath(), fileName + "-train-%d.bin");
        DataSetIterator loadedTrain = new AsyncDataSetIterator(existingTrainingData);
        DataSetIterator existingTestData = new CustomDatasetIterator(pathBuilder.getTestDataPath(), fileName + "-test-%d.bin");
        DataSetIterator loadedTest = new AsyncDataSetIterator(existingTestData);


        logger.debug("Starting training");
        net.fit(loadedTrain, nEpochs);
        logger.info("Epoch " + net.getEpochCount() + " complete. Starting evaluation:");

        BaseEvaluation<?> evaluation = buildEvaluation(loadedTest, net);
        logger.info(evaluation.stats());
        uiServer.detach(statsStorage);
        try {
            statsStorage.close();
        } catch (IOException e) {
            logger.info("Error while closing stats storage " + statsStorage);
        }

    }

    private void buildTrainingDataIfNotPresent(IDataGenerator<T> dataGenerator) {
        File testDataPath = pathBuilder.getTrainingDataPath();
        if (!testDataPath.exists()) {
            buildAndPersistTrainingData(dataGenerator);
        } else {
            File[] files = testDataPath.listFiles();
            if (files == null || files.length == 0) {
                buildAndPersistTrainingData(dataGenerator);
            }
        }
    }

    private MultiLayerNetwork readOrBuildNetwork() {
        File dir = pathBuilder.getCheckpointsPath();
        MultiLayerNetwork net;
        File[] checkpointFiles = dir.listFiles();
        if (checkpointFiles != null
                && checkpointFiles.length > 0
                && CheckpointListener.lastCheckpoint(dir) != null) {
            net = CheckpointListener.loadCheckpointMLN(dir, CheckpointListener.lastCheckpoint(dir));
        } else {
            net = buildNetwork();
        }
        return net;
    }


    private void buildAndPersistTrainingData(IDataGenerator<T> dataGenerator) {
        List<T> trainingSet = dataGenerator.generate();

        if (trainingSet == null) {
            return;
        }

        Random random = new Random(System.currentTimeMillis());

        int testSetSize = trainingSet.size() * 20 / 100;


        List<T> filteredTrainingSet = new ArrayList<>(trainingSet);
        List<T> testSet = new ArrayList<>();

        for (int j = 0; j < testSetSize; j++) {
            T entry = trainingSet.get(random.nextInt(filteredTrainingSet.size()));
            testSet.add(entry);
            filteredTrainingSet.remove(entry);
        }
        Collections.shuffle(filteredTrainingSet);

        // DataSetIterators for training and testing respectively
        DataSetIterator train = buildTrainingSetIterator(filteredTrainingSet);
        DataSetIterator test = buildTestSetIterator(testSet);

        File trainFolder = new File(pathBuilder.getTrainingDataPath(), "/");
        trainFolder.mkdirs();
        File testFolder = pathBuilder.getTestDataPath();
        testFolder.mkdirs();
        logger.info("Saving train data to " + trainFolder.getAbsolutePath() + " and test data to " + testFolder.getAbsolutePath());
        //Track the indexes of the files being saved.
        //These batch indexes are used for indexing which minibatch is being saved by the iterator.

        int trainDataSaved = 0;
        int testDataSaved = 0;
        while (train.hasNext()) {
            train.next().save(new File(trainFolder, fileName + "-train-" + trainDataSaved + ".bin"));

            trainDataSaved++;
        }

        while (test.hasNext()) {
            test.next().save(new File(testFolder, fileName + "-test-" + testDataSaved + ".bin"));
            testDataSaved++;
        }
    }

    protected abstract BaseEvaluation<?> buildEvaluation(DataSetIterator test, MultiLayerNetwork net);

    protected abstract DataSetIterator buildTrainingSetIterator(List<T> trainingSet);

    protected abstract DataSetIterator buildTestSetIterator(List<T> testSet);

    protected abstract MultiLayerNetwork buildNetwork();

    protected abstract int getBatchSize();


    private static class CustomDatasetIterator extends ExistingMiniBatchDataSetIterator {

        public CustomDatasetIterator(File rootDir, String pattern) {
            super(rootDir, pattern);
        }

        @Override
        public int totalOutcomes() {
            return 1;
        }
    }


}
